package Movable

interface Movable {
    var x: Int
    var y: Int
    var cur_speed: Int

    fun moveTo(_toX: Int, _toY: Int)
}