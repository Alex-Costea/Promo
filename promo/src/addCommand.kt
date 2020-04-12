package PromoCompiler

internal class addCommand(x:MutableList<Int>,leftBy:Int): commandInterface{
	override val name="add"
	var list=x
	var leftBy=leftBy
	constructor(x:Int) : this(mutableListOf(x),0)
	override fun show_data():String
	{
		return name+(if(leftBy>0) " $leftBy" else "")+" ("+list.fold(""){ a, b-> "$a $b"}.drop(1)+")"
	}

	override fun deep_copy(): addCommand {
		return addCommand(list,leftBy)

	}

	override fun is_equal(command: commandInterface): Boolean {
		if(command !is addCommand) return false
		val com=command as addCommand
		if(com.list!=list) return false
		if(com.leftBy!=leftBy) return false
		return true
	}

	override fun read_data(data: String) {
		var data2=data.split(" ").toMutableList()
		if(data2[0]!= "($name")
			throw Exception("Syntax Error")
		when {
			data2[1][0]=='(' -> {
				leftBy=0
				data2=data2.drop(1).toMutableList()

			}
			data2[2][0]=='(' -> {
				leftBy=data2[1].toIntOrNull()?:throw Exception("Syntax Error")
				data2=data2.drop(2).toMutableList()
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