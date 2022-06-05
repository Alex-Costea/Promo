package PromoCompiler

internal class moveCommand(x:Int): commandInterface {
	override val name="move"
	public var value:Int = x
	override fun show_data():String
	{
		return name+" "+value.toString()
	}

	override fun deep_copy(): moveCommand {
		return moveCommand(value)
	}

	override fun is_equal(command: commandInterface): Boolean {
		if(command !is moveCommand) return false
		val com=command as moveCommand
		if(com.value!=value) return false
		return true
	}

	override fun read_data(data: String)
	{
		var data2=data.split(" ")
		if(data2[0]!= "($name")
			throw Exception("Syntax Error")
		value = data2[1].dropLast(1).toIntOrNull() ?: throw Exception("Syntax Error")
	}
}