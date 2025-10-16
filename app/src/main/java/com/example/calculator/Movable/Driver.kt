package Movable

class Driver(_name: String, _surname: String, _second_name: String, _age: Int, _cur_speed: Int): Human(_name , _surname , _second_name , _age, _cur_speed, 0)
{
    fun straightMove() {
        x += cur_speed
        y += cur_speed
        println("Driver $name with speed $cur_speed moved. Current position: $x, $y")
    }
}