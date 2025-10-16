package Movable

import java.util.Random

open class Human : Movable
{
    var name: String = ""
    var surname: String = ""
    var second_name: String = ""
    var age: Int = 0
    var group_number: Int = -1
    override var cur_speed: Int = 1
    override var x = 0
    override var y = 0

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

    override fun moveTo(_toX: Int, _toY: Int) {
        x  += _toX * cur_speed
        y += _toY * cur_speed
        println("Human $name with speed $cur_speed moved on $_toX by x and $_toY by y. Current position: $x, $y")
    }

    open fun printFIO() {
        println("$surname $name $second_name, age: $age, group: $group_number")
    }
}