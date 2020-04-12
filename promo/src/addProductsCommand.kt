package PromoCompiler

internal class addProductsCommand(x:MutableList<Int>,div:Int?,leftBy:Int): commandInterface{

    override val name="add_products"
    var list=x
    var leftBy=leftBy
    var div=div
    override fun show_data():String
    {
        return name+" "+(div?:"n")+(if(leftBy>0) " $leftBy" else "")+" ("+list.fold(""){ a, b-> "$a $b"}.drop(1)+")"
    }

    override fun deep_copy(): addProductsCommand {
        return addProductsCommand(list,div,leftBy)

    }

    override fun is_equal(command: commandInterface): Boolean {
        if(command !is addProductsCommand) return false
        val com=command as addProductsCommand
        if(com.list!=list) return false
        if(com.leftBy!=leftBy) return false
        if(com.div!=div) return false
        return true
    }

    override fun read_data(data: String) {
        var data2=data.split(" ").toMutableList()
        if(data2[0]!= "($name")
            throw Exception("Syntax Error")
        if(data2[1]=="n")
            div=null
        else div=data2[1].toIntOrNull()?:throw Exception("Syntax Error")
        when {
            data2[2][0]=='(' -> {
                leftBy=0
                data2=data2.drop(2).toMutableList()

            }
            data2[3][0]=='(' -> {
                leftBy=data2[2].toIntOrNull()?:throw Exception("Syntax Error")
                data2=data2.drop(3).toMutableList()
            }
            else -> throw Exception("Syntax Error")
        }
        data2[0]=data2[0].drop(1)
        data2[data2.size-1]=data2[data2.size-1].dropLast(2)
        list= mutableListOf()
        for(x in data2)
        {
            list.add(x.toInt())
        }
    }
}