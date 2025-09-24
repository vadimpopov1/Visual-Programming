import java.util.Random

open class Human
{
    var name: String = ""
    var surname: String = ""
    var second_name: String = ""
    var age: Int = 0
    var group_number: Int = -1
    var cur_speed: Int = 1
    var x = 0
    var y = 0

    constructor(_name: String, _surname: String, _second_name: String, _age: Int, _cur_speed: Int, _group_name: Int ) {
        name = _name
        surname = _surname
        second_name = _second_name
        age = _age
        cur_speed = _cur_speed
        group_number = _group_name
        println("We created the Human object with name: $name")
    }

    open fun randomWalk() {
        val random = Random()
        val rand_x = random.nextInt(-1, 1)
        val rand_y = random.nextInt(-1, 1)
        when (this) {
            is Driver -> straightMove()
            is Human -> moveTo(rand_x, rand_y)
        }
    }

    open fun moveTo(_toX: Int, _toY: Int) {
        x  += _toX * cur_speed
        y += _toY * cur_speed
        println("Human $name with speed $cur_speed moved on $_toX by x and $_toY by y. Current position: $x, $y")
    }

    open fun printFIO() {
        println("$surname $name $second_name, age: $age, group: $group_number")
    }
}

class Driver(_name: String, _surname: String, _second_name: String, _age: Int, _cur_speed: Int): Human(_name , _surname , _second_name , _age, _cur_speed, 0)
{
    fun straightMove() {
        x += cur_speed
        y += cur_speed
        println("Driver $name with speed $cur_speed moved. Current position: $x, $y")
    }
}

fun main() {

//    val humans = arrayOf(
//        Human("Алексей", "Иванов", "Сергеевич", 23, 1, 444),
//        Human("Дмитрий", "Петров", "Александрович", 24, 1, 444),
//        Human("Михаил", "Федоров", "Дмитриевич", 18,1, 444),
//        Human("Иван", "Смирнов", "Игоревич", 19, 2,444),
//        Human("Артем", "Кузнецов", "Олегович", 20,2, 444),
//        Human("Сергей", "Попов", "Викторович", 31, 2,444),
//        Human("Андрей", "Васильев", "Николаевич", 19,3, 444),
//        Human("Павел", "Павлов", "Андреевич", 19, 6,555),
//        Human("Максим", "Голубев", "Артемович", 20,5, 555),
//        Human("Никита", "Воробьев", "Максимович", 22, 7,555),
//        Human("Кирилл", "Титов", "Павлович", 28, 8,555),
//        Human("Егор", "Королев", "Кириллович", 29,1, 555),
//        Human("Константин", "Белов", "Егорович", 26, 2,555),
//        Human("Юрий", "Медведев", "Константинович",23,3, 555),
//        Human("Владимир", "Антонов", "Романович", 22,4, 666),
//        Human("Олег", "Тихонов", "Владимирович", 23, 5,666),
//        Human("Глеб", "Федоров", "Олегович", 25, 6,666),
//        Human("Юрий", "Щербаков", "Глебович", 22,7, 666),
//        Human("Виктор", "Борисов", "Юрьевич", 29,8, 666),
//        Human("Станислав", "Орлов", "Викторович", 31,9, 666),
//        Human("Тимофей", "Комаров", "Станиславович", 19,10,666)
//    )

    val objects = arrayOf(
        Human("Михаил", "Федоров", "Дмитриевич", 18,1, 444),
        Human("Иван", "Смирнов", "Игоревич", 19, 2,444),
        Driver("Тимофей", "Комаров", "Станиславович", 19,40)
    )

    for (person in objects)
    {
        person.printFIO()
    }

    println("\nEnter count of seconds. (While this time humans will walk!)")
    val input = readln()
    val seconds = try {
        input.toInt().coerceAtLeast(0)
    } catch (e: NumberFormatException) {
        println("Invalid input. Use Int value or not least 1! \n Using default value = 1.")
        1
    }

    val threads = mutableListOf<Thread>()
    for (person in objects) {
        val thread = Thread {
            repeat(seconds) {
                person.randomWalk()
                Thread.sleep(1000)
            }
        }
        threads.add(thread)
    }

    threads.forEach { it.start() }

    threads.forEach { it.join() }

    println("\nAll objects walk!")
}
