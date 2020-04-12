package PromoCompiler

internal class foreverCommand():commandInterface
{
    override val name="forever"
    override fun deep_copy(): foreverCommand {
        return foreverCommand()
    }

    override fun show_data(): String {
        return name
    }

    override fun is_equal(command: commandInterface): Boolean {
        if(command !is foreverCommand) return false
        return true
    }

    override fun read_data(data: String) = pass
}