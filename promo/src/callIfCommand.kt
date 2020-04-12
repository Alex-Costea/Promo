package PromoCompiler

internal class callIfCommand(x:Int,dynamic:Boolean): commandInterface {
	override val name="call_if"
	var dynamic:Boolean = dynamic
	public var value:Int = x

	private fun dynamic_names(x:Boolean):String = if(x) " dynamic" else ""

	override fun show_data():String
	{
		return name+""+dynamic_names(dynamic)+" "+value.toString()
	}

	override fun deep_copy(): callIfCommand {
		return callIfCommand(value,dynamic)
	}
	override fun is_equal(command: commandInterface): Boolean {
		if(command !is callIfCommand) return false
		val com=command as callIfCommand
		if(com.dynamic!=dynamic) return false
		if(com.value!=value) return false
		return true
	}

	override fun read_data(data: String) {
		var data2=data.split(" ")
		if(data2[0]!= "($name")
			throw Exception("Syntax Error")
		if(data2.size==2)
		{
			dynamic = false
			value=data2[1].dropLast(1).toInt()
		}
		else if(data2[1]=="dynamic")
		{
			dynamic = true
			value=data2[2].dropLast(1).toInt()
		}
		else throw Exception("Syntax Error")
	}
}