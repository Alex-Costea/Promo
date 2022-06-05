package PromoCompiler

internal object bbcompiler {
	
	private enum class command_names{
		Plus,
		Minus,
		Left,
		Right,
		CallIf,
		DynamicCallIf
	}
	
	internal fun abs(x:Int):Int
    {
        if(x<0) return -x
        return x
    }
	
	private fun char2com_name(x:Char):command_names{
        when(x)
        {
            '+' -> return command_names.Plus
            '-' -> return command_names.Minus
            '>' -> return command_names.Right
            '<' -> return command_names.Left
            '#' -> return command_names.CallIf
            '@' -> return command_names.DynamicCallIf
            else -> throw Exception("not a valid character")
        }
	}
	
	private fun com_name2com(x:command_names,value:Int):commandInterface
	{
        when(x) {
            command_names.Plus -> return addCommand(value)
            command_names.Minus -> return addCommand(-value)
            command_names.Right -> return moveCommand(value)
            command_names.Left -> return moveCommand(-value)
            command_names.CallIf -> return callIfCommand(value, false)
            command_names.DynamicCallIf -> return callIfCommand(-value, true)
            else -> throw Exception("not a valid command")
        }
	}

	private fun valid_chars(x:command_names):String
	{
        when(x) {
            command_names.Plus -> return "+"
            command_names.Minus -> return "-"
            command_names.Right -> return ">"
            command_names.Left -> return "<"
            command_names.CallIf -> return "#@"
            command_names.DynamicCallIf -> return "@"
            else -> throw Exception("not a valid command")
        }
	}

	private fun Promo2UnoptimizedBBCL(code:String):programCommand
	{
		val bbcode=code.filter{it in "+-<>#@"}
		val funList=bbcode.split("+-")
		val xMode = bbcode.take(2)=="><"
		var program=programCommand(xMode)
		var fn=0
		for (x in funList)
		{
			program.programBody.add(funCommand(fn))
			var str=x+"!"
			var cur_com:command_names
			var i=0
			while(i<str.length)
			{
				if(str[i]=='!')
					break
				cur_com=char2com_name(str[i])
				var lasti=i
				var j=1
				while(str[i+1] in valid_chars(cur_com))
				{
					i+=1
					j+=1
				}

				i+=1
				if(cur_com==command_names.CallIf) {
					var substring = x.substring(lasti, i)
					substring = substring.replace("#", "1").replace("@","0")
					j=substring.toInt(2)
				}
				if(cur_com==command_names.DynamicCallIf)
					program.programBody[fn].body.add(moveCommand(j))
				program.programBody[fn].body.add(com_name2com(cur_com,j))
				if(cur_com==command_names.DynamicCallIf)
					program.programBody[fn].body.add(moveCommand(-j))

			}
			fn+=1
		}
		return program
	}

	private fun inline(program:programCommand)
	{
		var edges=mutableListOf<MutableList<Pair<Int,Int>>>()
		val size=program.programBody.last().functionNumber
		var j=0
		for(i in 0 .. size) {
			var references=mutableListOf<Pair<Int,Int>>()
			var dynamicCommandFound=false
			if(program.programBody[j].functionNumber>i)
            {
                continue
            }

			val calls=program.programBody[j].get_calls(size)

			if(calls==null)
			{
				edges.add((program.programBody).map{Pair(it.functionNumber,1)}.toMutableList())
			}
			else
			{
				edges.add(calls!!.mapIndexed{i,it->Pair(i,it)}.toMutableList())
			}
			j+=1
		}
		val inlines=inliner(edges,program.programBody.map{it.body.size},size).inlines
        for(x in inlines)
        {
			var second=x.second
			replace(program.programBody[x.first],program.programBody[x.second])
        }
	}

	private fun replace(inside:commandInterface,to:funCommand):commandInterface
	{
		var inside=inside
		if(inside is callIfCommand && !inside.dynamic && inside.value==to.functionNumber)
		{
			val new=to.deep_copy()
			val new_if=ifCommand(3)
			new_if.body=new.body
			inside=new_if
		}
		else if(inside is bodyInterface)
		{
			inside.body=inside.body.map{replace(it,to)}.toMutableList()
		}
		return inside
	}

	private fun combineAdds(program: programCommand)
	{
		program.programBody=program.programBody.map{
			var result:funCommand
			if(it.body.size>0)
			{
				result=funCommand(it.functionNumber)
				var moveBy:Int=0
				var negativeList=mutableListOf<Int>()
				var positiveList=mutableListOf<Int>()
				var zeroPosition=0
				for(i in 0 until it.body.size)
				{
					if((it.body[i] is addCommand && (it.body[i] as addCommand).list.size==1)|| it.body[i] is moveCommand)
					{
						if(it.body[i] is addCommand)
						{
							val value= (it.body[i] as addCommand).list[(it.body[i] as addCommand).leftBy]
							val absMoveBy=abs(moveBy)
							when
							{
								moveBy > 0 ->
								{
									while(absMoveBy>positiveList.size)
									{
										positiveList.add(0)
									}
									positiveList[absMoveBy-1]+=value
								}

								moveBy < 0 ->
								{

									while(absMoveBy>negativeList.size)
									{
										negativeList.add(0)
									}
									negativeList[absMoveBy-1]+=value
								}

								moveBy==0 ->
								{
									zeroPosition+=value
								}
							}
						}
						else
						{
							moveBy+=(it.body[i] as moveCommand).value
						}
					}
					else
					{
						if(i>0)
						{
							if(it.body[i-1] is addCommand || it.body[i-1] is moveCommand)
							{

								//add
								negativeList.reverse()
								var addedList=negativeList+mutableListOf(zeroPosition)+positiveList
								var leftBy=negativeList.size
								while(addedList.first()==0 && leftBy>0)
								{
									if(addedList.size==1) break
									addedList=addedList.drop(1)
									leftBy--
								}

								while(addedList.last()==0 && addedList.size-1>leftBy)
								{
									if(addedList.size==1) break
									addedList=addedList.dropLast(1)
								}

								if(!(addedList.size==1 && addedList[0]==0))
									result.body.add(addCommand(addedList.toMutableList(),leftBy))

								//move
								if(!(moveBy==0))
								result.body.add(moveCommand(moveBy))

								//reset
								moveBy=0
								negativeList=mutableListOf<Int>()
								positiveList=mutableListOf<Int>()
								zeroPosition=0
							}
						}
						result.body.add(it.body[i])
					}
				}
				val i=it.body.size-1
				if(i>=0) if (it.body[i] is addCommand || it.body[i] is moveCommand) {

					//add
					negativeList.reverse()
					var addedList = negativeList + mutableListOf(zeroPosition) + positiveList
					var leftBy = negativeList.size
					while (addedList.first() == 0 && leftBy > 0) {
						if(addedList.size==1) break
						addedList = addedList.drop(1)
						leftBy--
					}

					while (addedList.last() == 0 && addedList.size - 1 > leftBy) {
						if(addedList.size==1) break
						addedList = addedList.dropLast(1)
					}

					if (!(addedList.size==1 && addedList[0]==0))
						result.body.add(addCommand(addedList.toMutableList(), leftBy))

					//move
					if (!(moveBy == 0))
						result.body.add(moveCommand(moveBy))

				}
			}
			else result=it
			result
		}.toMutableList()
	}

	private fun removeCallsToNothing(program:programCommand)
	{
		val a = program.programBody.map { it.functionNumber }
		for (x in program.programBody)
			x.body = x.body.filter { !(it is callIfCommand && !it.dynamic && it.value !in a) }.toMutableList()
	}

	private fun removeUseless(program: programCommand)
	{
		var called_functions0=program.get_calls(program.programBody.last().functionNumber)
		if(called_functions0!=null) {
			var called_functions=called_functions0.map{it>0}
			var index = -1
			program.programBody = program.programBody.filter {
				index = index + 1
				called_functions[index] || index == 0
			}.toMutableList()
		}
	}

	private fun optimize_simple(program:programCommand)
	{
		program.programBody=program.programBody.map{
			var final=it
			val fn=it.functionNumber
			val first_call=it.body.indexOfFirst { (it is callIfCommand) && (it.value==fn) }
			when
			{
				first_call== -1 -> pass
				first_call == 0 ->
				{
					var new_function=funCommand(it.functionNumber)
					new_function.body.add(foreverCommand())
					final=new_function
				}
				else ->
				{
					// to loops
					var loop1=repeatCommand(1,loop.While)
					var loop2=repeatCommand(1,loop.For)
					var separator=false
					it.body.forEach{
						var separated=false
						if(it is callIfCommand && !separator)
						{
							if(!it.dynamic) if(it.value==fn)
							{
								separator = true
								separated = true
							}
						}
						if(!separated)
						{
							if (separator)
							{
								loop2.body.add(it)
							}
							else
							{
								loop1.body.add(it)
							}
						}
					}
					var new_function=funCommand(it.functionNumber)

					//to addProducts
					if(loop1.body.size==1 && loop1.body[0] is addCommand)
					{
						val k=loop1.body[0]
						if(k is addCommand)
						{
							var new_loop=addProductsCommand(k.list,abs(k.list[k.leftBy]),k.leftBy)
							when
							{
								k.list[k.leftBy]>0 ->
								{
									var new_if=ifCommand(1,If.Positive)
									new_if.body.add(foreverCommand())
									new_function.body.add(new_if)
									new_function.body.add(new_loop)
								}
								k.list[k.leftBy]<0 ->
								{
									var new_if=ifCommand(1,If.Negative)
									new_if.body.add(foreverCommand())
									new_function.body.add(new_if)
									new_function.body.add(new_loop)
								}
								else ->
								{
									var new_if=ifCommand(1)
									new_if.body.add(foreverCommand())
									new_function.body.add(new_if)
									new_function.body.add(new_loop)
								}
							}
						}
					}
					else new_function.body.add(loop1)

					if(loop2.body.size==1 && loop2.body[0] is addCommand)
					{
						val k=loop2.body[0]
						if(k is addCommand)
						{
							var new_loop=addProductsCommand(k.list,null,k.leftBy)
							new_function.body.add(new_loop)
						}
					}
					else if(loop2.body.isNotEmpty())
					{
						new_function.body.add(loop2)
					}
					final=new_function
				}
			}
			final
		}.toMutableList()
	}

	internal fun Promo2BBCL(code:String):programCommand //Promo Compiled Language
	{
		var program = Promo2UnoptimizedBBCL(code)
		var last_program:programCommand
		do {
			last_program=program.deep_copy()
			removeCallsToNothing(program)
			inline(program)
			combineAdds(program)
			removeUseless(program)
			optimize_simple(program)
			program.deep_set_level(1)
		} while(!program.is_equal(last_program))
		program.programBody = program.programBody.filter { it.body.size > 0 || it.functionNumber == 0 }.toMutableList()
		return program
	}
	
}
