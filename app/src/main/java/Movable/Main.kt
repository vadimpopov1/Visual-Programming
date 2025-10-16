package Movable

fun main() {
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

    val threads = objects.map { person ->
        Thread {
            repeat(seconds) {
                person.randomWalk()
                Thread.sleep(1000)
            }
        }
    }

    threads.forEach { it.start() }

    threads.forEach { it.join() }

    println("\nAll objects walk!")
}
