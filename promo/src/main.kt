import PromoCompiler.*
import org.w3c.dom.*
import kotlin.browser.*
import kotlin.dom.*
import kotlin.js.*

var running : Boolean? = null
var program : Promo? = null
lateinit var input : HTMLTextAreaElement
lateinit var output : HTMLTextAreaElement
lateinit var runButton : HTMLButtonElement
lateinit var stopButton : HTMLButtonElement
lateinit var inputx : HTMLInputElement
lateinit var output_result : HTMLInputElement
var inited=false
lateinit var worker : Worker

fun init()
{
	input = document.getElementById("input") as HTMLTextAreaElement
	output = document.getElementById("output") as HTMLTextAreaElement
	runButton = document.getElementById("run") as HTMLButtonElement
	stopButton = document.getElementById("stop") as HTMLButtonElement
	inputx = document.getElementById("x") as HTMLInputElement
	output_result = document.getElementById("result") as HTMLInputElement
	inited=true
}

fun toStr()
{
	if(!inited)
		init()
	output.value=Promo.toBBCL(input.value)
	runButton.disabled=false
	stopButton.disabled=true
	running=false
	val mode=output.value.replace("\n"," ").split(" ")[1]
	val x=mode=="x"
	val i=mode=="i"
	when {
		i -> {
			inputx.disabled=true
			inputx.value="0"
		}
		x -> inputx.disabled=false
		else -> throw Exception("Syntax Error")
	}
}


suspend fun run()
{
	if(running==false)
	{
		worker=Worker("js/worker.js")
		running=true
		runButton.disabled=true
		stopButton.disabled=false
		val first=output.value
		val second=inputx.value.replace(0.toChar().toString(),"").trim()
		worker.postMessage(first+0.toChar()+second)
		worker.onmessage=fun(e)
		{
			output_result.value=e.data as String
			worker.terminate()
			running=false
			runButton.disabled=false
			stopButton.disabled=true
		}
	}
}

suspend fun halt()
{
	if(running==true)
	{
		worker.terminate()
		running=false
		runButton.disabled=false
		stopButton.disabled=true
	}
}
