// =====================================================================
// MainActivity.kt
// ---------------------------------------------------------------------
// Este arquivo tem o app inteiro. Ele está dividido em 3 partes:
//
//   1. PONTO DE ENTRADA  -> MainActivity (o Android abre ela primeiro)
//   2. FRONTEND (UI)     -> PostListPage e PostListTile (telas em Compose)
//   3. BACKEND (dados)   -> PostListItem (modelo) e fetchPosts (rede)
//
// Fluxo: MainActivity abre -> mostra PostListPage -> PostListPage chama
// fetchPosts() -> fetchPosts baixa o JSON da internet e devolve uma lista
// de PostListItem -> a tela desenha um PostListTile para cada post.
// =====================================================================

// "package" = o "endereço" deste arquivo dentro do projeto.
// Tem que bater com a pasta (com/example/feedsocialmedia).
package com.example.feedsocialmedia

// ---------------------------------------------------------------------
// IMPORTS: avisam ao Kotlin de onde vem cada classe/função usada aqui.
// Se um nome ficar vermelho no Android Studio, quase sempre falta o import.
// (Os imports cinza no Android Studio são os que não estão sendo usados.)
// ---------------------------------------------------------------------
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.experimental.Experimental
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.feedsocialmedia.ui.theme.FeedSocialMediaTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.URL
import kotlin.time.Duration.Companion.seconds


// #####################################################################
// 1. PONTO DE ENTRADA
// #####################################################################

/**
 * MainActivity
 * ------------
 * A "Activity" é uma tela do Android. Esta é a tela principal: o
 * AndroidManifest.xml diz que ela é a LAUNCHER, ou seja, é a que abre
 * quando o usuário toca no ícone do app.
 *
 * Herda de ComponentActivity, que é a Activity usada com Jetpack Compose.
 */
class MainActivity : ComponentActivity() {

    /**
     * onCreate
     * --------
     * Função chamada pelo Android quando a tela é criada (o "main" do app).
     *
     * @param savedInstanceState dados salvos se a tela foi recriada
     *        (ex.: ao girar o celular). Na primeira abertura vem null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        // Obrigatório: deixa a classe pai fazer a inicialização dela primeiro.
        super.onCreate(savedInstanceState)

        // Faz o app desenhar "de ponta a ponta", por trás da barra de status
        // e da barra de navegação. Por isso depois usamos o innerPadding.
        enableEdgeToEdge()

        // setContent { } diz: "o conteúdo desta tela é escrito em Compose".
        // Tudo que estiver aqui dentro é a interface.
        setContent {
            // Aplica o tema do app (cores e fontes, definidos em ui/theme).
            FeedSocialMediaTheme {
                // A página que lista os posts (definida logo abaixo).
                PostListPage()
            }
        }
    }


}


// #####################################################################
// 2. FRONTEND (interface em Jetpack Compose)
// ---------------------------------------------------------------------
// Funções marcadas com @Composable descrevem um pedaço da tela.
// Quando um "estado" que elas leem muda, o Compose redesenha a tela
// sozinho (isso se chama recomposição).
// #####################################################################

/**
 * PostListPage
 * ------------
 * A tela inteira: busca os posts quando aparece e mostra uma de três
 * coisas: mensagem de erro, "carregando" ou a lista de posts.
 */
@Composable
fun PostListPage() {

    // posts: a lista de posts baixada da API.
    //  - mutableStateOf(...)  cria um ESTADO: quando o valor muda, a tela redesenha.
    //  - remember { }         guarda esse estado entre os redesenhos
    //                         (sem ele, o valor voltaria para null a cada redesenho).
    //  - "by"                 deixa usar "posts" direto, em vez de "posts.value".
    //  - List<PostListItem>?  o "?" quer dizer que pode ser null.
    //                         Aqui null significa "ainda não carregou".
    var posts by remember { mutableStateOf<List<PostListItem>?>(null) }

    // errorMessage: texto do erro, se a busca falhar.
    // null = não houve erro.
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // LaunchedEffect: roda um código de fundo (coroutine) quando a tela aparece.
    // A chave "Unit" nunca muda, então isso roda UMA vez só, e não a cada redesenho.
    // É aqui que o FRONTEND chama o BACKEND.
    LaunchedEffect(Unit) {
        try {

            // Chama a função de rede. Ela é "suspend": a tela não trava enquanto
            // espera. Quando termina, guardamos o resultado no estado "posts",
            // e isso faz a tela redesenhar mostrando a lista.
            posts = fetchPosts()
        } catch (e: Exception) {
            // Se algo der errado (sem internet, JSON inválido...), guardamos a
            // mensagem no estado "errorMessage". A tela redesenha mostrando o erro.
            // e.message = a descrição do erro gerada pelo Kotlin/Java.
            errorMessage = "Ops, something went wrong ( ${e.message} )"
        }
    }
    /**
     * if errorMessage -> Text(ErrorMessage)
     * else if posts == null -> loading
     * else List(posts)
     * */
    // Scaffold: a "estrutura base" de uma tela Material (pode ter topBar,
    // botão flutuante etc.). Aqui usamos só o corpo.
    //  - Modifier.fillMaxSize()  ocupa a tela toda.
    //  - innerPadding            espaço que o Scaffold manda deixar livre
    //                            para não ficar embaixo das barras do sistema.
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        // when { } é o "if / else if / else" do Kotlin.
        // O primeiro caso verdadeiro é o que aparece na tela.
        when {
            // Caso 1: houve erro -> mostra o texto do erro.
            // O "!!" diz ao Kotlin: "tenho certeza que não é null aqui".
            errorMessage != null -> Text(errorMessage!!)

            // Caso 2: ainda não chegou a lista -> mostra a bolinha girando
            // (CircularProgressIndicator = indicador de carregamento).
            posts == null -> CircularProgressIndicator(modifier = Modifier.padding(innerPadding))

            // Caso 3: a lista chegou -> desenha a lista.
            // LazyColumn = lista vertical que só desenha os itens visíveis na
            // tela (equivale ao RecyclerView). Boa para listas grandes.
            else -> LazyColumn(
                Modifier
                    .fillMaxSize()              // ocupa a tela toda
                    .padding(innerPadding),     // respeita as barras do sistema
                // 16dp de espaço entre um post e outro.
                // dp = unidade de tamanho que se adapta à densidade da tela.
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {

                // items(...): para cada post da lista, cria um item na LazyColumn.
                //  - posts ?: emptyList()  "se posts for null, usa lista vazia"
                //                          (operador Elvis ?:).
                //  - key = { it.id }       identifica cada item pelo id do post;
                //                          ajuda o Compose a não redesenhar à toa.
                //  - post ->               o post da vez, usado dentro das chaves.
                items(posts ?: emptyList(), key = { it.id }) { post ->
                    // Desenha o "cartão" de um post (função logo abaixo).
                    PostListTile(post)

                }
            }
        }
    }
}

/**
 * PostListTile
 * ------------
 * Desenha UM post da lista: cabeçalho, título e corpo, um embaixo do outro.
 *
 * @param post o post que vai ser mostrado (vem do items(...) acima).
 */
@Composable
fun PostListTile(post: PostListItem) {
    // Column empilha os filhos na vertical.
    // fillMaxWidth() = ocupa toda a largura, mas só a altura que precisa.
    Column(Modifier.fillMaxWidth()) {

        // Linha de cabeçalho. ${...} coloca o valor da variável dentro do texto
        // (string template).
        Text("User Id: ${post.userId} ° Post #${post.id}")

        // Título do post, com a fonte "headlineSmall" do tema (maior, de título).
        Text(
            post.title,
            style = MaterialTheme.typography.headlineSmall
        )

        // Corpo do post. maxLines = 3 corta o texto depois de 3 linhas.
        Text(
            post.body,
            maxLines = 3
        )
    }
}


// #####################################################################
// 3. BACKEND (dados e rede)
// ---------------------------------------------------------------------
// Nada aqui sabe que existe uma tela. Esta parte só busca e organiza
// os dados. A tela é quem chama fetchPosts().
// #####################################################################

/**
 * PostListItem
 * ------------
 * MODELO de um post: o "molde" que representa cada item do JSON da API.
 * "data class" = classe feita só para guardar dados. O Kotlin já gera
 * equals, toString e copy automaticamente.
 *
 * Cada campo corresponde a uma chave do JSON de
 * https://jsonplaceholder.typicode.com/posts
 */
data class PostListItem(
    val userId: Int,    // id do usuário que escreveu o post
    val id: Int,        // id único do post (usado como "key" na LazyColumn)
    val title: String,  // título do post
    val body: String    // texto (conteúdo) do post
)

/**
 * fetchPosts
 * ----------
 * Baixa a lista de posts da API e converte o JSON em List<PostListItem>.
 *
 * - "suspend": função de coroutine. Pode "pausar" esperando a rede sem
 *   travar a tela. Só pode ser chamada de dentro de outra coroutine
 *   (como o LaunchedEffect lá em cima).
 * - withContext(Dispatchers.IO): roda o bloco numa thread própria para
 *   rede/disco. O Android PROÍBE rede na thread principal
 *   (daria NetworkOnMainThreadException).
 * - O valor da última linha do bloco é o que a função devolve.
 *
 * @return a lista de posts. Se algo falhar, lança uma exceção, que é
 *         capturada pelo try/catch em PostListPage.
 */
suspend fun fetchPosts(): List<PostListItem> = withContext(Dispatchers.IO) {
    // Espera 4 segundos de propósito, só para dar tempo de VER o "carregando"
    // na tela. Num app de verdade, esta linha seria removida.
    delay(4.seconds)

    // jsonRaw: o texto cru da resposta, ou seja, o JSON inteiro como String.
    // URL(...).readText() faz um GET simples e lê tudo.
    val jsonRaw = URL("https://jsonplaceholder.typicode.com/posts").readText()

    // jsonList: transforma o texto em um array JSON ([ {...}, {...}, ... ])
    // que dá para percorrer item por item.
    val jsonList = JSONArray(jsonRaw)

    // Cria uma List com o mesmo tamanho do array JSON.
    // Para cada posição (index = 0, 1, 2...) o bloco { } monta um PostListItem.
    // Esta lista é o valor devolvido pela função.
    List<PostListItem>(jsonList.length()) { index ->
        // jsonObject: o post da posição atual, ainda em formato JSON.
        val jsonObject = jsonList.getJSONObject(index)

        // Copia cada campo do JSON para o nosso modelo (data class).
        PostListItem(
            // get() devolve um "Any" (qualquer tipo); o "as Int" converte para Int.
            // Equivale a getInt("userId"), que é a forma mais segura.
            userId = jsonObject.get("userId") as Int,
            id = jsonObject.getInt("id"),           // lê a chave "id" como Int
            title = jsonObject.getString("title"),  // lê a chave "title" como String
            body = jsonObject.getString("body")     // lê a chave "body" como String
        )
    }
}



