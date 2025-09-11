class Human
{
    var name: String = ""
    var surname: String = ""
    var second_name: String = ""
    var group_number: Int = -1
    var x = 0
    var y = 0

    constructor(_name: String, _surname: String, _second_name: String, _group_name: Int ) {
        name = _name
        surname = _surname
        second_name = _second_name
        group_number = _group_name
        println("We created the Human object with name: $name")
    }

    fun move() {
        println("Human in moved")
    }

    fun moveTo(_toX: Int, _toY: Int) {
        x  = _toX
        y = _toY
        println("Human is moved TO: $x, $y")
    }
}

fun main() {

    val petya: Human = Human("Petya", "Ivanov", "Petrovich", 444)
    petya.move()
    petya.moveTo(10, 100)
    println("${petya.name}")

}
