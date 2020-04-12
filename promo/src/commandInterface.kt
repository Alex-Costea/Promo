package PromoCompiler

internal interface commandInterface {
	val name:String
	fun show_data():String
	fun deep_copy():commandInterface
	fun is_equal(command: commandInterface):Boolean
	fun read_data(data:String)
}