## ИКС-433 Попов Вадим Александрович

### RANDOM WALK
Принцип работы:
1. Начальные координаты (x,y) = (0,0)
2. Каждую секунду человек рандомно перемещается по x на 1, 0 или -1 (вверх/остается на месте/вниз) по y на 1, 0 или -1 (вперед/остается на месте/назад)
3. По указаному направлению он перемещается по заранее заданой скорости например если направления 1,1 и скорость 2 то он переместится по каждой коориднате на 2.
4. В зависимости от типа объекта (Человек или Водитель), будет задано движение (RandomWalk или прямолинейное движение).

        class Human
        {
            ..
            var cur_speed: Int = 1 (значение по умолчанию)
            var x = 0
            var y = 0
            ..

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
        }

### Многопоточность:

1. Каждый объект двигается в отдельном потоке
2. Все перемещения синхронизированы по времени (1 шаг в секунду)
3. Основной поток ждет завершения всех перемещений с помощью join()

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
