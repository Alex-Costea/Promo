// import dependecies in the JS file

import PromoCompiler.BigInt
import PromoCompiler.Promo
import kotlin.browser.window

fun run_worker(program:String,input:String) {
    val program = Promo(program)
    val x = program.run_samethread(BigInt(input)).toString()
    js("postMessage(x)")
}

fun main()
{
    //remove the "window" in JS when compiling
    window.onmessage=fun(it){
        val values=(it.data as String).split(0.toChar())
        run_worker(values[0],values[1])
    }
}

//run main() in JS
