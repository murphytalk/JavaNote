package murphytalk

import murphytalk.test.StopWatch
import org.junit.Test
class TestCollection{


    @Test
    fun `add to list from map one by one`(){
        StopWatch.measureSimple("one_by_one", {
            val lst = mutableListOf<Long>(0)
            for((k,v ) in o){
                lst.add(k)
                lst.add(v)
            }
        }, repeat)
    }

    @Test
    fun `seq and flatten`(){
        StopWatch.measureSimple("seq_flatten", {
            val lst = mutableListOf(0L).addAll(o.asSequence().map { listOf(it.key, it.value) }.flatten())
        }, repeat)
    }

    companion object {
        private fun fibonacci(): Sequence<Long> {
            // fibonacci terms
            // 0, 1, 1, 2, 3, 5, 8, 13, 21, 34, 55, 89, 144, 233, 377, 610, 987, 1597, 2584, 4181, 6765, 10946, ...
            return generateSequence(Pair(0L, 1L), { Pair(it.second, it.first + it.second) }).map { it.first }
        }
        private val o = fibonacci().take(10000).associateBy { it }
        private const val repeat = 20000L
    }
}

class TestOneByOneVsCollapsing{
    private fun operation(e: Map<String,Int>):Int { return e.asSequence().fold(0) {acc, (_,v) -> acc+v }}

    data class D(val id:String, val data:MutableMap<String,Int>)

    @Test
    fun `one by one for`(){
        StopWatch.measureSimple("1_by_1_for",{
            for(d in stuff){ operation(d.data) }
        }, repeat)
    }

    @Test
    fun `one by one foreach`(){
        StopWatch.measureSimple("1_by_1_foreach",{
            stuff.forEach {  operation(it.data) }
        }, repeat)
    }

    @Test
    fun `group by list`(){
        StopWatch.measureSimple("group_by_list",{
            val l = mutableListOf<Pair<String,D>>()
            stuff.forEach{
                val last = l.find { f -> f.first == it.id }
                if (last!=null){
                    last.second.data.putAll(it.data)
                }
                else
                {
                    l.add(Pair(it.id, it))
                }
            }
            l.forEach{operation(it.second.data)}
        }, repeat)
    }

    @Test
    fun `group by map`(){
        StopWatch.measureSimple("group_by_map",{
            val l = mutableMapOf<String, D>()
            stuff.forEach{
                val last = l[it.id]
                if (last!=null){
                    last.data.putAll(it.data)
                }
                else
                {
                    l[it.id] = it
                }
            }
            l.forEach{ (_, v) -> operation(v.data)}
        }, repeat)
    }

    @Test
    fun `group by kotlin grouping`(){
        StopWatch.measureSimple("group_by_kotlin_grouping",{
            val l = stuff.groupingBy { it.id }.fold(
                    {_,d -> d.data},
                    {_, acc, element -> acc.also{  it.putAll(element.data)} })
            l.forEach{ (_, v) -> operation(v)}
        }, repeat)
    }

    companion object{
        private const val repeat = 1_000_000L

        private val stuff = listOf(
                D("1", mutableMapOf( "f1" to 100, "f2" to 200, "f3" to 300)),
                D("2", mutableMapOf( "f1" to 100,              "f3" to 300)),
                D("2", mutableMapOf(              "f2" to 200, "f3" to 300)),
                D("1", mutableMapOf(                           "f3" to 300, "f4" to 400)),
                D("3", mutableMapOf( "f1" to 110)),
                D("2", mutableMapOf( "f1" to 110,              "f3" to 310))
        )
    }
}