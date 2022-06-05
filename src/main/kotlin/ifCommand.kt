package PromoCompiler

internal enum class If{
    NotZero,
    Positive,
    Negative
}

internal class ifCommand(level:Int,pos:If) : bodyInterface {
    override var name="if"
    override var body = mutableListOf<commandInterface>()
    override var level=level
    var pos=pos
    constructor(level:Int):this(level,If.NotZero)
    override fun show_data():String
    {
        var data=name
        if(pos!=If.NotZero)
        {
            data+=" "+pos.name.toLowerCase()
        }
        data+="\n"
        data+=body.map{"("+it.show_data()+")"+"\n"}.fold(""){a,b->a+b}.dropLast(1)
        return data
    }

    override fun deep_copy(): ifCommand {
        val new = ifCommand(level,pos)
        new.body=body.map{it.deep_copy()}.toMutableList()
        return new
    }

    override fun is_equal(command: commandInterface): Boolean {
        if(command !is ifCommand) return false
        val com=command as ifCommand
        if(com.level!=level) return false
        if(com.pos!=pos) return false
        val x=body.mapIndexed{index,it->it.is_equal(com.body[index])}.fold(true){a,b->a&&b}
        if(!x) return false
        return true
    }

    override fun read_data(data: String) {
        var data2=data.split(" ")
        if(data2[0]!= "($name")
            throw Exception("Syntax Error")
        pos = when {
            data2[1][0] == '(' -> If.NotZero
            data2[2][0] == '(' ->
            {
                when {
                    data2[1] == "positive" -> If.Positive
                    data2[1] == "negative" -> If.Negative
                    else -> throw Exception("Syntax Error")
                }
            }
            else -> throw Exception("Syntax Error")
        }
        read_body(data)
    }
}
