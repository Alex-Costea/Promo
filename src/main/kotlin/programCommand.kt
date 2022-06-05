package PromoCompiler

internal class programCommand(xMode:Boolean?): bodyInterface {
	override val name="program"
	public var programBody:MutableList<funCommand> = mutableListOf()
	public override var body: MutableList<commandInterface>
		get() =programBody as MutableList<commandInterface>
		set(value) {programBody=value as MutableList<funCommand>}
	public var xMode:Boolean? = xMode
	override var level=1
	
	constructor():this(false)

	override fun show_data():String
	{
		var data:String="("+name+" "+(if(xMode!!) "x" else "i")+"\n"
		data+=body.map{"("+it.show_data()+")"+"\n"}.fold(""){a,b->a+b}.dropLast(1)
		return "$data)"
	}

	override fun deep_copy(): programCommand {
		val new = programCommand(xMode)
		new.level=level
		new.body=body.map{it.deep_copy()}.toMutableList()
		return new

	}

	override fun is_equal(command: commandInterface): Boolean {
		if(command !is programCommand) return false
		val com=command as programCommand
		if(com.level!=level) return false
		if(com.xMode!=xMode) return false
		val x=body.mapIndexed{index,it->it.is_equal(com.body[index])}.fold(true){a,b->a&&b}
		if(!x) return false
		return true
	}

	override fun read_data(data: String) {
		var my_data=data
		my_data=my_data.replace("\n"," ")
		my_data=my_data.replace("\\s+".toRegex(), " ")
		my_data=my_data.replace(") (", ")(")
		my_data=my_data.replace(") )", "))")
		my_data=my_data.replace("( (", "((")
		var data2=my_data.split(" ")
		if(data2[0]!= "($name")
			throw Exception("Syntax Error")
		if(data2[1]=="x")
			xMode=true
		else if(data2[1]=="i")
			xMode=false
		else throw Exception("Syntax Error")
		read_body(my_data)
		deep_set_level(1)
	}
}