package PromoCompiler

import kotlin.math.max

class BigInt(x:String){

    var isEmpty=false
    var positive=true
    var elements=mutableListOf<Boolean>()
    constructor(x:Int) : this(x.toString())
    fun toIntOrNull()=this.toString().toIntOrNull()
    operator fun plus(x:Int) = plus(BigInt(x))
    fun divides(div:Int) : Boolean = divides(BigInt(div))
    fun divides(div: BigInt) = this/div*div==this
    operator fun div(x: Int) = div(BigInt(x))
    operator fun compareTo(i: Int) = compareTo(BigInt(i))
    operator fun times(x: Int) = times(BigInt(x))
    private fun isZero(z:BigInt) = z.elements.size==1 && !z.elements[0]

    constructor(x:BigInt) : this(0)
    {
        this.isEmpty=x.isEmpty
        this.elements=x.elements.toMutableList()
        this.positive=x.positive
    }

    init{
        var x=x.trim()
        if(x=="" || x=="-")
            isEmpty = true
        when(x[0])
        {
            '-' ->
            {
                positive=false
                x=x.drop(1)
            }
            '+' ->
            {
                positive=true
                x=x.drop(1)
            }
        }
        elements.add(false)
        if(x!="0")
        {
            for (i in x)
            {
                val y = i.toString().toIntOrNull()
                val ten=BigInt(0)
                ten.elements= mutableListOf(true,false,true,false)
                val yBigNum=BigInt(0)
                if (y == null)
                {
                    isEmpty = true
                    break
                }
                else
                {
                    yBigNum.elements= when(y)
                    {
                        0 -> mutableListOf(false)
                        1 -> mutableListOf(true)
                        2 -> mutableListOf(true,false)
                        3 -> mutableListOf(true,true)
                        4 -> mutableListOf(true,false,false)
                        5 -> mutableListOf(true,false,true)
                        6 -> mutableListOf(true,true,false)
                        7 -> mutableListOf(true,true,true)
                        8 -> mutableListOf(true,false,false,false)
                        9 -> mutableListOf(true,false,false,true)
                        else -> throw Exception()
                    }
                    var new=BigInt(this).abs()
                    new=new*ten+yBigNum
                    this.elements=new.elements
                }
            }
        }
        if(isEmpty)
        {
            positive=true
            elements= mutableListOf(false)
        }
    }

    override operator fun equals(other: Any?): Boolean {
        return isZero(this - (other as BigInt))
    }

    operator fun minus(other: BigInt): BigInt {
        var other=BigInt(other)
        other.positive=!other.positive
        return this+other
    }

    fun abs(): BigInt {
        val x=BigInt(this)
        x.positive=true
        return x
    }

    operator fun compareTo(i: BigInt): Int {
        if(this==i)
            return 0
        return if((this-i).positive) 1 else -1
    }

    private fun Base10Add1(Base10:MutableList<Int>):MutableList<Int>
    {
        var base10= (listOf(0) + Base10).toMutableList()
        base10[base10.size-1]+=1
        for(i in base10.size-1 downTo 0)
        {
            if(base10[i]>9)
            {
                base10[i-1]++
                base10[i]=base10[i]-10
            }
        }
        val rez = base10.dropWhile{it==0}.toMutableList()
        if(rez.size==0)
            rez.add(0)
        return rez
    }

    private fun Base10Multiply2(Base10:MutableList<Int>):MutableList<Int>
    {
        var base10= (listOf(0) + Base10).toMutableList()
        var carry=false
        for(i in base10.size-1 downTo 0)
        {
            base10[i]*=2
            if(carry)
                base10[i]++
            carry=false
            if(base10[i]>9)
            {
                carry=true
                base10[i]=base10[i]-10
            }
        }
        val rez = base10.dropWhile{it==0}.toMutableList()
        if(rez.size==0)
            rez.add(0)
        return rez
    }

    override fun toString(): String
    {
        if(isEmpty)
            return "NaN"
        var Base10=mutableListOf(0)
        for(x in elements)
        {
            Base10=Base10Multiply2(Base10)
            if(x)
                Base10=Base10Add1(Base10)

        }
        var result=if(positive) "" else "-"
        for(x in Base10)
        {
            result += x.toString()
        }
        return result
    }

    private fun remove_leading_zeroes(y:BigInt)
    {
        y.elements = y.elements.dropWhile{ !it }.toMutableList()
        if(y.elements.size==0)
            y.elements.add(false)
    }

    operator fun plus(x: BigInt): BigInt
    {
        if(!positive && !x.positive)
        {
            var x1=this
            var x2=x
            x1.positive=true
            x2.positive=true
            var res=x1+x2
            res.positive=false
            return res
        }
        else if(!positive && x.positive)
        {
            return x+this
        }
        else if(x.positive)
        {
            //y+=z
            var y=BigInt(this)
            var z=BigInt(x)
            var max=max(y.elements.size,z.elements.size)+1
            var carrying=false
            y.elements=(List(max-y.elements.size){false}+y.elements).toMutableList()
            z.elements=(List(max-z.elements.size){false}+z.elements).toMutableList()
            for(i in y.elements.size-1 downTo 0)
            {
                val a = y.elements[i]
                val b = z.elements[i]
                if(!carrying)
                {

                    y.elements[i] = a xor b
                    carrying = a and b
                }
                else
                {
                    y.elements[i] = !(a xor b)
                    carrying = a or b
                }
            }
            remove_leading_zeroes(y)
            return y
        }
        else
        {
            var y=BigInt(this).abs()
            var z=BigInt(x).abs()
            var biggerOrEqual=true
            var max=max(y.elements.size,z.elements.size)

            // if abs(y)<abs(z), swap
            if(y.elements.size>z.elements.size)
                biggerOrEqual=true
            else if(y.elements.size<z.elements.size)
                biggerOrEqual=false
            else
            {
                for (i in 0 until max)
                {
                    if (y.elements[i] && !z.elements[i])
                        break
                    if (!y.elements[i] && z.elements[i])
                    {
                        biggerOrEqual = false
                        break
                    }
                }
            }
            if (!biggerOrEqual)
            {
                var temp = y
                y = z
                z = temp
            }

            //subtraction
            var carrying=false
            z.elements=(List(max-z.elements.size){false}+z.elements).toMutableList()
            for(i in y.elements.size-1 downTo 0)
            {
                val a = y.elements[i]
                val b = z.elements[i]
                if(!carrying)
                {
                    y.elements[i] = a xor b
                    carrying = !a and b
                }
                else
                {
                    y.elements[i] = !(a xor b)
                    carrying= !a or b
                }
            }
            remove_leading_zeroes(y)
            if(!biggerOrEqual)
                y.positive=false
            return y
        }
    }

    operator fun times(x: BigInt): BigInt
    {
        var final=BigInt(0)
        var current=BigInt(this)
        if(isZero(x) || isZero(this))
            return BigInt(0)
        if(this.elements.size<x.elements.size)
            return x*this
        for(i in x.elements.reversed())
        {
            if(i)
                final+=current
            current.elements.add(false)
        }
        final.positive=this.positive==x.positive
        return final
    }

    operator fun div(x: BigInt): BigInt
    {
        val num_digs=this.elements.size-x.elements.size+1
        var num=BigInt(0)
        if(num_digs<1)
            return num
        num.positive=this.positive==x.positive
        var y=this.abs()
        var z=x.abs()
        if(z==BigInt(1))
        {
            num.elements=this.elements
            return num
        }
        if(isZero(z))
            throw ArithmeticException()
        num.elements=List(num_digs){false}.toMutableList()
        for(i in 0 until num.elements.size)
        {
            num.elements[i]=true
            if(num*z>y)
                num.elements[i]=false
        }
        remove_leading_zeroes(num)
        return num
    }

    override fun hashCode(): Int {
        var result = isEmpty.hashCode()
        result = 31 * result + positive.hashCode()
        result = 31 * result + elements.hashCode()
        return result
    }
}