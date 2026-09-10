import express from "express";
import cors from "cors";
import dotenv from "dotenv";
import OpenAI from "openai";

dotenv.config();
const app=express();
app.use(cors());
app.use(express.json({limit:"10mb"}));
const model=process.env.OPENAI_MODEL || "gpt-5.6-luna";
const client=new OpenAI({apiKey:process.env.OPENAI_API_KEY});
app.get("/health",(req,res)=>res.json({ok:true,name:"DONNE 3GB IA",model}));
app.post("/api/chat",async(req,res)=>{try{const message=String(req.body.message||"").trim();if(!message)return res.status(400).json({error:"message_required"});const r=await client.responses.create({model,instructions:"Tu es DONNE 3GB IA, assistant professionnel en français. Réponds clairement, utilement et de façon concise.",input:message});res.json({text:r.output_text});}catch(e){console.error(e);res.status(500).json({error:"openai_request_failed",detail:e.message});}});
app.post("/api/analyze-image",async(req,res)=>{try{const b64=String(req.body.imageBase64||"");if(!b64)return res.status(400).json({error:"image_required"});const r=await client.responses.create({model,input:[{role:"user",content:[{type:"input_text",text:String(req.body.prompt||"Analyse cette image et décris précisément ce qui est visible en français.")},{type:"input_image",image_url:`data:image/jpeg;base64,${b64}` }]}]});res.json({text:r.output_text});}catch(e){console.error(e);res.status(500).json({error:"image_analysis_failed",detail:e.message});}});
app.post("/api/quote",(req,res)=>{const items=Array.isArray(req.body.items)?req.body.items:[];const labor=Number(req.body.labor||0);const subtotal=items.reduce((s,x)=>s+Number(x.qty||0)*Number(x.unitPrice||0),0);res.json({items,labor,subtotal,total:subtotal+labor,currency:"F CFA"});});
app.listen(Number(process.env.PORT||3000),()=>console.log(`DONNE 3GB IA server running on ${process.env.PORT||3000}`));
