package PromoCompiler

internal class inliner(list:MutableList<MutableList<Pair<Int,Int>>>,size:List<Int>,n:Int)
{
    private var list:MutableList<MutableList<Int>> = list.map{it.filter{it.second>0}.map{it.first}.toMutableList()}.toMutableList()
    private var complete_list = list

    private val n=n
    private var size=size.toMutableList()

    var inlines=inline(0, mutableListOf())
        get
        private set

    private fun nr_calls(first: Int,second: Int):Int
    {
        val x=complete_list[first].indexOfFirst {  it.first==second}
        if(x==-1) return 0
        return complete_list[first][x].second
    }

    private fun get_cycles(list:MutableList<MutableList<Int>>,stack:List<Int>,cycled:MutableList<Boolean>):MutableList<Boolean>
    {
        val last=stack.dropLast(1).lastOrNull()
        var cycled=cycled
        val current=stack.last()
        var current_list=list[current]
        var stack2=stack.dropLast(1)
        for(y in current_list)
        {
            if(last!=null)
            {
                if(!(y in stack2))
                {
                    cycled=get_cycles(list,stack+listOf(y),cycled)
                }
                else
                {
                    cycled[current]=true
                    var i=stack.size-1
                    while(stack[i]!=y)
                    {
                        cycled[stack[i]]=true
                        i--
                    }
                    cycled[stack[i]]=true
                }
            }
            else
            {
                cycled=get_cycles(list,stack+listOf(current),cycled)
            }
        }
        return cycled
    }

    private fun inline(current:Int,stack:List<Int>):MutableList<Pair<Int,Int>>
    {
        var x=mutableListOf<Pair<Int,Int>>()
        val last=stack.lastOrNull()
        val general_cycles=get_cycles(list,listOf(0),MutableList(n+1){false})
        var current_list=list[current].toList()
        for(y in current_list)
        {
            if((y in list[current]) && last!=null)
            {
                if(!(y in stack))
                    x=inline(y,stack+listOf(current))
            }
            else x=inline(y,stack+listOf(current))
        }
        if(last!=null && should_inline(last,current))
        {
            var first=last
            var second=current
            var removable=true
            val current_list=list[current].toList()
            for(third in current_list)
            {
                var new_list=list.map{it.toMutableList()}.toMutableList()
                new_list[first].remove(second)
                new_list[second].remove(third)
                val cycled=get_cycles(new_list, listOf(0),MutableList(n+1){false})
                if (second==third || first==third || (general_cycles[second] && !(general_cycles[first]&&general_cycles[third]) && !cycled[second]) )
                {

                    removable=false
                }
            }
            if(removable)
            {
                for (third in current_list) {
                    list[first].remove(second)
                    list[second].remove(third)
                    if (!(third in list[first]))
                        list[first].add(third)

                }
                size[first]=size[first]+nr_calls(first,second)*size[second]
                x = (x + mutableListOf(Pair(first, second))).toMutableList()
            }
        }
        return x.toMutableList()
    }

    private fun should_inline(first: Int, second: Int): Boolean {
        if(first==-1)
            return false
        return size[first]+nr_calls(first,second)*size[second]<=100
    }
}