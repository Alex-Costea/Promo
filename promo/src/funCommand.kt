package PromoCompiler

internal class funCommand(x:Int): bodyInterface {
	override val name="procedure"
	companion object {
		val mainFunName = "main"
	}
	var functionNumber:Int = x
	override var body:MutableList<commandInterface> =mutableListOf()
	override var level=2

	override fun show_data():String
	{
		var data=name+" "+(if (functionNumber!=0) functionNumber.toString() else mainFunName)+"\n"
		data+=body.map{"("+it.show_data()+")"+"\n"}.fold(""){a,b->a+b}.dropLast(1)
		return data
	}

	override fun deep_copy(): funCommand {
		val new = funCommand(functionNumber)
		new.level=level
		new.body=body.map{it.deep_copy()}.toMutableList()
		return new
	}

	override fun is_equal(command: commandInterface): Boolean {
		if(command !is funCommand) return false
		val com=command as funCommand
		if(com.level!=level) return false
		if(com.functionNumber!=functionNumber) return false
		val x=body.mapIndexed{index,it->it.is_equal(com.body[index])}.fold(true){a,b->a&&b}
		if(!x) return false
		return true
	}

	override fun read_data(data: String) {
		var data2=data.split(" ")
		if(data2[0]!= "($name")
			throw Exception("Syntax Error")
		if(data2[1]== mainFunName)
		{
			functionNumber=0
		}
		else
		{
			functionNumber = data2[1].toIntOrNull() ?: throw Exception("Syntax Error")
		}
		read_body(data)
	}
}