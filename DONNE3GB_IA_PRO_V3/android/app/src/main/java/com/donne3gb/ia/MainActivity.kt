package com.donne3gb.ia

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import org.json.JSONObject
import org.json.JSONArray
import android.util.Base64

private const val API_URL = "http://10.0.2.2:3000"

data class QuoteItem(var name:String, var qty:Int, var unitPrice:Int)

data class ChatMsg(val who:String, val text:String)

class MainActivity: ComponentActivity() {
    private var selectedImageUri by mutableStateOf<Uri?>(null)
    private var voiceText by mutableStateOf("")
    private var voiceLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        if (r.resultCode == Activity.RESULT_OK) {
            voiceText = r.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: ""
        }
    }
    private val imageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri -> selectedImageUri = uri }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Donne3GBApp(
                this,
                selectedImageUri,
                voiceText,
                onPickImage = { imageLauncher.launch("image/*") },
                onVoice = {
                    val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "fr-FR")
                    }
                    voiceLauncher.launch(i)
                },
                onClearVoice = { voiceText = "" }
            )
        }
    }
}

@Composable
fun Donne3GBApp(ctx:Context, imageUri:Uri?, voiceText:String, onPickImage:()->Unit, onVoice:()->Unit, onClearVoice:()->Unit) {
    var tab by remember { mutableIntStateOf(0) }
    Scaffold(bottomBar={ NavigationBar {
        NavigationBarItem(tab==0,{tab=0},{Text("IA")})
        NavigationBarItem(tab==1,{tab=1},{Text("Devis")})
        NavigationBarItem(tab==2,{tab=2},{Text("Outils")})
    }}) { p ->
        when(tab) {
            0 -> ChatScreen(Modifier.padding(p), ctx, imageUri, onPickImage)
            1 -> QuoteScreen(Modifier.padding(p), ctx)
            else -> ToolsScreen(Modifier.padding(p), voiceText, onVoice, onClearVoice, imageUri, onPickImage)
        }
    }
}

@Composable
fun ChatScreen(mod:Modifier, ctx:Context, imageUri:Uri?, onPickImage:()->Unit) {
    var input by remember { mutableStateOf("") }
    var busy by remember { mutableStateOf(false) }
    var msgs by remember { mutableStateOf(listOf(ChatMsg("IA","👋 Bienvenue dans DONNE 3GB IA !"))) }
    Column(mod.padding(16.dp).fillMaxSize()) {
        Row(verticalAlignment=Alignment.CenterVertically) {
            Image(painterResource(R.drawable.donne3gb_logo),"DONNE 3GB",Modifier.size(68.dp))
            Spacer(Modifier.width(10.dp)); Column { Text("DONNE 3GB IA",style=MaterialTheme.typography.headlineSmall); Text("Assistant professionnel") }
        }
        Spacer(Modifier.height(10.dp))
        LazyColumn(Modifier.weight(1f)) { items(msgs) { m -> Text("${m.who} : ${m.text}",Modifier.padding(7.dp)) } }
        Row {
            OutlinedTextField(input,{input=it},Modifier.weight(1f),placeholder={Text("Pose ta question...")})
            Spacer(Modifier.width(6.dp)); Button(enabled=!busy,onClick={
                val q=input.trim(); if(q.isNotEmpty()) {
                    msgs=msgs+ChatMsg("Vous",q); input=""; busy=true
                    Thread { val ans=postJson("$API_URL/api/chat", JSONObject().put("message",q));
                        (ctx as Activity).runOnUiThread { msgs=msgs+ChatMsg("IA",ans); busy=false }
                    }.start()
                }
            }) { Text(if(busy)"..." else "Envoyer") }
        }
        Spacer(Modifier.height(6.dp))
        OutlinedButton(onClick=onPickImage,modifier=Modifier.fillMaxWidth()){ Text(if(imageUri==null) "📷 Analyser une photo" else "📷 Photo sélectionnée — analyser") }
        if(imageUri!=null) Text("Photo prête pour l'analyse. Utilise le bouton ci-dessus puis l'outil Photo.", style=MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun QuoteScreen(mod:Modifier,ctx:Context) {
    var client by remember{mutableStateOf("")}
    var labor by remember{mutableStateOf("75000")}
    val rows=remember{mutableStateListOf(
        QuoteItem("Tuyau Ø11 mm – rouleau 100 m",1,24500),
        QuoteItem("Tuyau Ø13 mm – rouleau 100 m",1,29500),
        QuoteItem("Tuyau Ø16 mm – rouleau 100 m",1,38500),
        QuoteItem("Tuyau Ø21 mm – rouleau 50 m",1,31700),
        QuoteItem("Accessoires de pose",1,15000)
    )}
    var status by remember{mutableStateOf("")}
    val subtotal=rows.sumOf{it.qty*it.unitPrice}; val total=subtotal+(labor.toIntOrNull()?:0)
    Column(mod.padding(16.dp).fillMaxSize()) {
        Text("Devis professionnel",style=MaterialTheme.typography.headlineSmall)
        OutlinedTextField(client,{client=it},label={Text("Nom du client")},modifier=Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        LazyColumn(Modifier.weight(1f)) {
            items(rows) { r ->
                Row(verticalAlignment=Alignment.CenterVertically,modifier=Modifier.fillMaxWidth().padding(vertical=4.dp)) {
                    OutlinedTextField(r.name,{v->r.name=v},Modifier.weight(1f),label={Text("Désignation")})
                    Spacer(Modifier.width(4.dp)); OutlinedTextField(r.qty.toString(),{v->r.qty=v.toIntOrNull()?:1},Modifier.width(65.dp),label={Text("Qté")})
                    Spacer(Modifier.width(4.dp)); OutlinedTextField(r.unitPrice.toString(),{v->r.unitPrice=v.toIntOrNull()?:0},Modifier.width(100.dp),label={Text("Prix")})
                }
            }
        }
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween){
            Button(onClick={rows.add(QuoteItem("Nouvel article",1,0))}){Text("+ Article")}
            Text("Sous-total: $subtotal F",modifier=Modifier.padding(12.dp))
        }
        OutlinedTextField(labor,{labor=it},label={Text("Main-d'œuvre (F CFA)")})
        Text("TOTAL : $total F CFA",style=MaterialTheme.typography.titleLarge)
        Button(onClick={status=generateQuotePdf(ctx,client,rows.toList(),labor.toIntOrNull()?:0)},modifier=Modifier.fillMaxWidth()){Text("📄 Générer le PDF")}
        if(status.isNotBlank()) Text(status,style=MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun ToolsScreen(mod:Modifier,voiceText:String,onVoice:()->Unit,onClearVoice:()->Unit,imageUri:Uri?,onPickImage:()->Unit){
    Column(mod.padding(16.dp).fillMaxSize()) {
        Text("Outils DONNE 3GB",style=MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        Button(onClick=onVoice,modifier=Modifier.fillMaxWidth()){Text("🎙️ Parler à DONNE 3GB IA")}
        if(voiceText.isNotBlank()){
            Text("Texte reconnu :",style=MaterialTheme.typography.titleMedium); Text(voiceText); TextButton(onClick=onClearVoice){Text("Effacer")}
        }
        Spacer(Modifier.height(8.dp))
        Button(onClick=onPickImage,modifier=Modifier.fillMaxWidth()){Text("📷 Choisir une photo")}
        if(imageUri!=null){Text("Image sélectionnée : $imageUri",style=MaterialTheme.typography.bodySmall)}
        Spacer(Modifier.height(16.dp))
        Text("Fonctions V3 : IA, devis dynamique, PDF, photo et commande vocale.")
        Text("La clé OpenAI reste sur le serveur et ne doit pas être intégrée dans l'APK.",style=MaterialTheme.typography.bodySmall)
    }
}

fun postJson(url:String, body:JSONObject):String = try {
    val c=URL(url).openConnection() as HttpURLConnection
    c.requestMethod="POST"; c.doOutput=true; c.setRequestProperty("Content-Type","application/json")
    c.outputStream.use{it.write(body.toString().toByteArray())}
    val text=(if(c.responseCode in 200..299)c.inputStream else c.errorStream).bufferedReader().readText()
    JSONObject(text).optString("text", JSONObject(text).optString("error","Erreur serveur"))
} catch(e:Exception){"Erreur réseau : ${e.message}"}

fun generateQuotePdf(ctx:Context,client:String,items:List<QuoteItem>,labor:Int):String{
    val doc=PdfDocument(); val page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,1).create()); val c=page.canvas; val p=Paint(Paint.ANTI_ALIAS_FLAG)
    val logo=BitmapFactory.decodeResource(ctx.resources,R.drawable.donne3gb_logo); c.drawBitmap(logo,null,android.graphics.Rect(35,30,190,105),p)
    p.typeface=Typeface.DEFAULT_BOLD;p.textSize=24f;c.drawText("DEVIS",430f,65f,p);p.typeface=Typeface.DEFAULT;p.textSize=11f
    c.drawText("FOURNITURE & POSE DE TUYAUX ÉLECTRIQUES",300f,85f,p);c.drawText("DONNE 3GB",35f,125f,p)
    c.drawText("Client : $client",35f,150f,p);c.drawText("Date : ${SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(Date())}",400f,150f,p)
    var y=185f;p.typeface=Typeface.DEFAULT_BOLD;c.drawText("DÉSIGNATION",35f,y,p);c.drawText("QTÉ",380f,y,p);c.drawText("MONTANT",445f,y,p);p.typeface=Typeface.DEFAULT
    for(i in items){y+=25;c.drawText(i.name.take(48),35f,y,p);c.drawText(i.qty.toString(),390f,y,p);c.drawText("${i.qty*i.unitPrice} F",445f,y,p)}
    val subtotal=items.sumOf{it.qty*it.unitPrice};y+=35;c.drawText("Sous-total",350f,y,p);c.drawText("$subtotal F CFA",445f,y,p);y+=25;c.drawText("Main-d'œuvre",350f,y,p);c.drawText("$labor F CFA",445f,y,p)
    y+=35;p.typeface=Typeface.DEFAULT_BOLD;c.drawText("TOTAL GÉNÉRAL",330f,y,p);c.drawText("${subtotal+labor} F CFA",445f,y,p);y+=50;p.typeface=Typeface.DEFAULT;c.drawText("Signature & cachet : ______________________________",35f,y,p)
    doc.finishPage(page);val dir=ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)!!;val file=File(dir,"devis_donne3gb_${System.currentTimeMillis()}.pdf");FileOutputStream(file).use{doc.writeTo(it)};doc.close();return "PDF créé : ${file.absolutePath}"
}
