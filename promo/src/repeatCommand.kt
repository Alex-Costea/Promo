package PromoCompiler

import PromoCompiler.loop.For as LoopFor
import PromoCompiler.loop.While as LoopWhile

internal enum class loop{
    For,
    While
}

internal class repeatCommand(level:Int,loop: loop) : bodyInterface {
    override var name="loop"
    override var body = mutableListOf<commandInterface>()
    override var level=level
    var loop=loop

    override fun show_data():String
    {
        var data=name+" "+loop.name.toLowerCase()+"\n"
        data+=body.map{"("+it.show_data()+")"+"\n"}.fold(""){a,b->a+b}.dropLast(1)
        return data
    }

    override fun deep_copy(): repeatCommand {
        val new = repeatCommand(level,loop)
        new.body=body.map{it.deep_copy()}.toMutableList()
        return new
    }

    override fun is_equal(command: commandInterface): Boolean {
        if(command !is repeatCommand) return false
        val com=command as repeatCommand
        if(com.level!=level) return false
        if(com.loop!=loop) return false
        val x=body.mapIndexed{index,it->it.is_equal(com.body[index])}.fold(true){a,b->a&&b}
        if(!x) return false
        return true
    }

    override fun read_data(data: String) {
        var data2=data.split(" ")
        if(data2[0]!= "($name")
            throw Exception("Syntax Error")
        if(data2[1]=="for")
        {
            loop= LoopFor
        }
        else if(data2[1]=="while")
        {
            loop= LoopWhile
        }
        else throw Exception("Syntax Error")
        read_body(data)
    }
}
