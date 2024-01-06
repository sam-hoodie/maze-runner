fun main() {
//    val path = makePath((getMaze()), mutableListOf(Cell(0, 0)), Cell(0, 0), mutableListOf())
//    printMaze(getMaze(), path)
//    printMaze(makeBlankMaze(3, 3), listOf())
//    var maze = convert(
//        listOf(
//            Wall(Cell(1, 0), WallDirection.HORIZONTAL, 2),
//            Wall(Cell(2, 0), WallDirection.VERTICAL, 2),
//            Wall(Cell(2, 1), WallDirection.HORIZONTAL, 2),
//            Wall(Cell(5, 0), WallDirection.VERTICAL, 2),
//            Wall(Cell(0, 2), WallDirection.VERTICAL, 2),
//            Wall(Cell(0, 3), WallDirection.HORIZONTAL, 2),
//            Wall(Cell(3, 3), WallDirection.VERTICAL, 3),
//            Wall(Cell(1, 5), WallDirection.HORIZONTAL, 3),
//            Wall(Cell(5, 3), WallDirection.VERTICAL, 2)
//        ),
//        Cell(0, 0), Cell(6, 6), 6, 6
//    )
////    printMaze(getMaze(), listOf())
//    maze = removeWalls(maze) as List<MutableList<Int>>
//    val path = makePath(maze, mutableListOf(Cell(0, 0)), Cell(0, 0), mutableListOf())
////    println(path)
////    printMaze(maze, path)
////    printMaze(removeWalls(maze), listOf())
//    printMaze(maze, path)

////    printMaze(removeWalls(convert(startingWalls, Cell(0, 0), Cell(5, 6), 5, 6)), listOf())
//    val walls = generateMaze(startingWalls, Cell(0, 0), Cell(5, 6), 5, 6)
//    val potential = getTravelPotential(convert(startingWalls, Cell(0, 0), Cell(5, 4), 5, 6), WallDirection.VERTICAL, Cell(5, 0))
//    println(potential)
//    printMaze(maze, listOf())
//    println(getPotential(maze, Cell(0, 1), WallDirectionCardinal.RIGHT))
//    println(getPotential(maze, Cell(0, 3), WallDirectionCardinal.LEFT))
    val start = Cell(1, 1)
    val end = Cell(15, 15)
    val width = 17
    val height = 17
    val startingWalls = getStartingWalls(width, height)
    val maze = generateMazeRecursive(startingWalls, start, end, width, height)
    printMaze(maze, listOf())
    val path = makePath(maze, mutableListOf(), start, mutableListOf())
    printMaze(maze, path)

//    val testMazeWalls = arrayListOf<Wall>()
//    testMazeWalls.addAll(getStartingWalls(10, 10))
//    testMazeWalls.addAll(
//        listOf(
//            Wall(Cell(0, 2), WallDirection.HORIZONTAL, 3),
//            Wall(Cell(0, 4), WallDirection.HORIZONTAL, 8),
//            Wall(Cell(4, 2), WallDirection.VERTICAL, 3),
//            Wall(Cell(7, 2), WallDirection.VERTICAL, 3),
//            Wall(Cell(2, 6), WallDirection.VERTICAL, 4),
//            Wall(Cell(2, 7), WallDirection.HORIZONTAL, 4),
//            Wall(Cell(4, 6), WallDirection.VERTICAL, 2),
//            Wall(Cell(7, 6), WallDirection.VERTICAL, 4)
//        )
//    )
//    val possibleWalls = getAllPossibleNewWalls(testMazeWalls, Cell(1, 1), Cell(8, 8), 10, 10)
//    println(possibleWalls.size)
}


data class Cell(
    val x: Int,
    val y: Int
)


enum class WallDirection {
    HORIZONTAL,
    VERTICAL
}

enum class WallDirectionCardinal {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

data class Wall(
    val start: Cell,
    val direction: WallDirection,
    val length: Int,
)

fun getAllPossibleNewWalls(walls: List<Wall>, start: Cell, end: Cell, width: Int, height: Int): List<Wall> {
    val maze = convert(walls, start, end, width, height)
    val result = arrayListOf<Wall>()
    for (wall in walls) {
        val horizontals = listOf(WallDirectionCardinal.LEFT, WallDirectionCardinal.RIGHT)
        val verticals = listOf(WallDirectionCardinal.UP, WallDirectionCardinal.DOWN)
        val directions = when (wall.direction) {
            WallDirection.HORIZONTAL -> verticals
            WallDirection.VERTICAL -> horizontals
        }
        for (i in 0 until wall.length) {
            val point = when (wall.direction) {
                WallDirection.VERTICAL -> Cell(wall.start.x, wall.start.y + i)
                WallDirection.HORIZONTAL -> Cell(wall.start.x + i, wall.start.y)
            }
            for (direction in directions) {
                val potential = getPotential(maze, point, direction)
                if (potential > 1) {
                    result.add(convertToHorOrVert(point, direction, potential))
                }
            }
        }
    }
    return result
}

fun generateMazeRecursive(walls: MutableList<Wall>, start: Cell, end: Cell, width: Int, height: Int): List<List<Int>> {
    val maze = convert(walls, start, end, width, height)
    // selects a random wall already part of the maze to try to grow from
    val possibleWalls = getAllPossibleNewWalls(walls, start, end, width, height)
    if (possibleWalls.isEmpty()) {
        return maze
    }
    val randomPossibleWall = possibleWalls[possibleWalls.indices.shuffled().last()]
    return generateMazeRecursive(appendWall(walls, randomPossibleWall), start, end, width, height)
}

fun generateMazeLoop(walls: MutableList<Wall>, start: Cell, end: Cell, width: Int, height: Int): List<List<Int>> {
    var maze = convert(walls, start, end, width, height)
    while (true) {
        val possibleWalls = getAllPossibleNewWalls(walls, start, end, width, height)
        if (possibleWalls.isEmpty()) {
            break
        }
        val randomWall = possibleWalls[possibleWalls.indices.shuffled().last()]
        walls.add(randomWall)
        maze = convert(walls, start, end, width, height)
    }
    return maze
}

fun getStartingWalls(width: Int, height: Int): MutableList<Wall> {
    return mutableListOf(
        Wall(Cell(0, 0), WallDirection.HORIZONTAL, width),
        Wall(Cell(0, 0), WallDirection.VERTICAL, height),
        Wall(Cell(0, height - 1), WallDirection.HORIZONTAL, width),
        Wall(Cell(width - 1, 0), WallDirection.VERTICAL, height)
    )
}

fun getPotential(maze: List<List<Int>>, start: Cell, direction: WallDirectionCardinal): Int {
    // if the point is on an outside wall and the selected direction does not result on the wall growing inwards,
    // it is given a potential of 1 (cannot be grown)
    if (start.x == 0 && direction != WallDirectionCardinal.RIGHT) {
        return 1
    }
    if (start.y == 0 && direction != WallDirectionCardinal.DOWN) {
        return 1
    }
    if (start.x == maze[0].size - 1 && direction != WallDirectionCardinal.LEFT) {
        return 1
    }
    if (start.y == maze.size - 1 && direction != WallDirectionCardinal.UP) {
        return 1
    }
    var size = 1
    // for every direction, the wall is grown one space at a time until it reaches a point in which a wall cell cannot be placed.
    if (direction == WallDirectionCardinal.RIGHT) {
        var i = start.x
        while (true) {
            val newCell = Cell(i + 1, start.y)
            if (isValidWallPlace(maze, newCell, direction)) {
                size++
                i++
            } else {
                break
            }
        }
    }
    if (direction == WallDirectionCardinal.DOWN) {
        var i = start.y
        while (true) {
            val newCell = Cell(start.x, i + 1)
            if (isValidWallPlace(maze, newCell, direction)) {
                size++
                i++
            } else {
                break
            }
        }
    }
    if (direction == WallDirectionCardinal.LEFT) {
        var i = start.x
        while (true) {
            val newCell = Cell(i - 1, start.y)
            if (isValidWallPlace(maze, newCell, direction)) {
                size++
                i--
            } else {
                break
            }
        }
    }
    if (direction == WallDirectionCardinal.UP) {
        var i = start.y
        while (true) {
            val newCell = Cell(start.x, i - 1)
            if (isValidWallPlace(maze, newCell, direction)) {
                size++
                i--
            } else {
                break
            }
        }
    }
    return size
}

fun convertToHorOrVert(start: Cell, direction: WallDirectionCardinal, length: Int): Wall {
    // horizontal: left or right
    // vertical: up or down
    return when (direction) {
        WallDirectionCardinal.RIGHT -> Wall(start, WallDirection.HORIZONTAL, length)
        WallDirectionCardinal.DOWN -> Wall(start, WallDirection.VERTICAL, length)
        WallDirectionCardinal.LEFT -> Wall(Cell(start.x - (length - 1), start.y), WallDirection.HORIZONTAL, length)
        WallDirectionCardinal.UP -> Wall(Cell(start.x, start.y - (length - 1)), WallDirection.VERTICAL, length)
    }
}

fun isValidWallPlace(maze: List<List<Int>>, position: Cell, direction: WallDirectionCardinal): Boolean {
    // this function checks if a wall can be placed in a specified cell
    // in the direction given, the conditions check the cells to the left, right, front, and diagonal front both directions
    // if that cell is a wall, the current cell cannot be a wall
    val current = maze[position.y][position.x]
    if (direction == WallDirectionCardinal.RIGHT) {
        return !((maze[position.y - 1][position.x + 1] == 1) || (maze[position.y][position.x + 1] == 1) ||
                (maze[position.y + 1][position.x + 1] == 1) || (maze[position.y - 1][position.x] == 1) ||
                (maze[position.y + 1][position.x] == 1) || current != 0)
    }
    if (direction == WallDirectionCardinal.DOWN) {
        return !((maze[position.y + 1][position.x + 1] == 1) || (maze[position.y + 1][position.x] == 1) ||
                (maze[position.y + 1][position.x - 1] == 1) || (maze[position.y][position.x + 1] == 1) ||
                (maze[position.y][position.x - 1] == 1) || current != 0)
    }
    if (direction == WallDirectionCardinal.LEFT) {
        return !((maze[position.y - 1][position.x - 1] == 1) || (maze[position.y][position.x - 1] == 1) ||
                (maze[position.y + 1][position.x - 1] == 1) || (maze[position.y - 1][position.x] == 1) ||
                (maze[position.y + 1][position.x] == 1) || current != 0)
    }
    if (direction == WallDirectionCardinal.UP) {
        return !((maze[position.y - 1][position.x - 1] == 1) || (maze[position.y - 1][position.x] == 1) ||
                (maze[position.y - 1][position.x + 1] == 1) || (maze[position.y][position.x + 1] == 1) ||
                (maze[position.y][position.x - 1] == 1) || current != 0)
    }
    return true
}

fun convert(walls: List<Wall>, start: Cell, end: Cell, width: Int, height: Int): List<MutableList<Int>> {
    // this function converts a set of walls and dimensions into a maze
    val maze = makeBlankMaze(height, width)
    // for every wall, the non-wall cells in a blank maze are replaced by a wall cell
    for (wall in walls) {
        for (i in 0 until wall.length) {
            if (wall.direction == WallDirection.HORIZONTAL) {
                maze[wall.start.y][wall.start.x + i] = 1
            } else {
                maze[wall.start.y + i][wall.start.x] = 1
            }
        }
    }
    // start and end are added using specified coordinates
    maze[start.y][start.x] = 2
    maze[end.y][end.x] = 3
    return maze
}

fun makeBlankMaze(height: Int, width: Int): MutableList<MutableList<Int>> {
    val result = mutableListOf<MutableList<Int>>()
    for (i in 1..height) {
        val row = mutableListOf<Int>()
        for (i2 in 1..width) {
            row.add(0)
        }
        result.add(row)
    }
    return result
}

//fun generateMaze(walls: MutableList<Wall>, start: Cell, end: Cell, width: Int, height: Int): List<Wall> {
//    val newMaze = convert(walls, start, end, width, height)
////    printMaze(removeWalls(convert(walls, start, end, width, height)), listOf())
////    println()
//    val randomWall = walls[(Math.random() * walls.size).toInt()]
//    val newWallDirection = inverseDirection(randomWall.direction)
//    println(randomWall)
//    println(newWallDirection)
//    val randomPoint = when (randomWall.direction) {
//        WallDirection.VERTICAL -> Cell(
//            randomWall.start.x,
//            randomWall.start.y + (2..randomWall.length - 2).shuffled().last()
//        )
//        WallDirection.HORIZONTAL -> Cell(
//            randomWall.start.x + (2..randomWall.length - 2).shuffled().last(),
//            randomWall.start.y
//        )
//    }
//    println(randomPoint)
//    println()
//    val potential = getTravelPotential(newMaze, newWallDirection, randomPoint)
//    if (potential == 1 || potential == 0) {
//        return generateMaze(walls, start, end, width, height)
//    }
//    val newWall = Wall(randomPoint, newWallDirection, potential, WallType.INNER)
//    printMaze(removeOuterWalls(newMaze), listOf())
//    println()
//    println()
//    return generateMaze(appendWall(walls, newWall), start, end, width, height)
//}
//
//fun getTravelPotential(maze: List<List<Int>>, direction: WallDirection, start: Cell): Int {
//    var length = 1
//    if (direction == WallDirection.VERTICAL && start.y == maze.size - 1) {
//        return 0
//    }
//    if (direction == WallDirection.HORIZONTAL && start.x == maze[0].size - 1) {
//        return 0
//    }
//    val visited = arrayListOf(start)
//    if (direction == WallDirection.VERTICAL) {
//        var i = start.y + 1
//        while (true) {
//            if (maze[i][start.x] == 2 || maze[i][start.x] == 3) {
//                return length
//            }
//            if (wallsAround(maze, Cell(start.x, i), visited)) {
//                break
//            }
//            visited.add(Cell(start.x, i))
//            length++
//            i++
//        }
//    }
//    if (direction == WallDirection.HORIZONTAL) {
//        var i = start.x + 1
//        while (true) {
//            if (maze[i][start.x] == 2 || maze[i][start.x] == 3) {
//                return length
//            }
//            if (wallsAround(maze, Cell(i, start.y), visited)) {
//                break
//            }
//            visited.add(Cell(start.y, i))
//            length++
//            i++
//        }
//    }
//    return length
//}
//
//fun wallsAround(maze: List<List<Int>>, position: Cell, ignoreCells: List<Cell>): Boolean {
//    var cellToCheck = Cell(position.x + 1, position.y)
//    val invalidSpots = listOf(1)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    cellToCheck = Cell(position.x + 1, position.y + 1)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    cellToCheck = Cell(position.x, position.y + 1)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    cellToCheck = Cell(position.x - 1, position.y)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    cellToCheck = Cell(position.x - 1, position.y - 1)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    cellToCheck = Cell(position.x, position.y - 1)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    cellToCheck = Cell(position.x + 1, position.y)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    cellToCheck = Cell(position.x + 1, position.y - 1)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    cellToCheck = Cell(position.x - 1, position.y + 1)
//    if (invalidSpots.contains(maze[cellToCheck.y][cellToCheck.x]) && !ignoreCells.contains(cellToCheck)) {
//        return true
//    }
//    return false
//}
//
//fun inverseDirection(direction: WallDirection): WallDirection {
//    return when (direction) {
//        WallDirection.VERTICAL -> WallDirection.HORIZONTAL
//        WallDirection.HORIZONTAL -> WallDirection.VERTICAL
//    }
//}
// // old code (unusable)

fun removeOuterWalls(maze: List<MutableList<Int>>): List<List<Int>> {
    val new = arrayListOf<MutableList<Int>>()
    new.addAll(maze)
    new.removeAt(0)
    new.removeAt(new.size - 1)
    for (i in new.indices) {
        new[i].removeAt(0)
        new[i].removeAt(new[i].size - 1)
    }
    return new
}


fun makeTopBottom(width: Int): MutableList<Int> {
    val result = mutableListOf(1, 1)
    for (i in 1..width) {
        result.add(1)
    }
    return result
}

fun getMaze(): List<List<Int>> {
    return arrayListOf(
        listOf(2, 1, 1, 1, 0, 1, 1, 0, 0, 0, 0),
        listOf(0, 0, 0, 1, 0, 0, 1, 0, 1, 0, 1),
        listOf(0, 1, 0, 1, 1, 0, 1, 0, 1, 0, 0),
        listOf(0, 1, 0, 0, 0, 0, 0, 0, 1, 1, 1),
        listOf(0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0),
        listOf(1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1),
        listOf(0, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1),
        listOf(0, 1, 1, 1, 0, 1, 0, 0, 0, 0, 0),
        listOf(0, 1, 0, 1, 0, 1, 0, 1, 1, 1, 0),
        listOf(0, 0, 0, 1, 0, 1, 0, 0, 1, 1, 3)
    )
}

fun makePath(maze: List<List<Int>>, path: MutableList<Cell>, currentPos: Cell, visited: MutableList<Cell>): List<Cell> {
    val x = currentPos.x
    val y = currentPos.y
    if (maze[y][x] == 3) {
        return path
    }
    if (canGo(maze, Direction.UP, currentPos) && !visited.contains(Cell(x, y - 1))) {
        return makePath(maze, appendCell(path, Cell(x, y - 1)), Cell(x, y - 1), appendCell(visited, currentPos))
    }
    if (canGo(maze, Direction.DOWN, currentPos) && !visited.contains(Cell(x, y + 1))) {
        return makePath(maze, appendCell(path, Cell(x, y + 1)), Cell(x, y + 1), appendCell(visited, currentPos))
    }
    if (canGo(maze, Direction.LEFT, currentPos) && !visited.contains(Cell(x - 1, y))) {
        return makePath(maze, appendCell(path, Cell(x - 1, y)), Cell(x - 1, y), appendCell(visited, currentPos))
    }
    if (canGo(maze, Direction.RIGHT, currentPos) && !visited.contains(Cell(x + 1, y))) {
        return makePath(maze, appendCell(path, Cell(x + 1, y)), Cell(x + 1, y), appendCell(visited, currentPos))
    }
    return makePath(maze, path.subList(0, path.size - 1), path[path.size - 2], appendCell(visited, currentPos))
}

fun appendCell(list: MutableList<Cell>, new: Cell): MutableList<Cell> {
    val newList = arrayListOf<Cell>()
    newList.addAll(list)
    newList.add(new)
    return newList
}

fun appendWall(list: MutableList<Wall>, new: Wall): MutableList<Wall> {
    val newList = arrayListOf<Wall>()
    newList.addAll(list)
    newList.add(new)
    return newList
}

// position = [x, y] where the first row is x = 0 and first column is y = 0
fun canGo(maze: List<List<Int>>, direction: Direction, position: Cell): Boolean {
    val x = position.x
    val y = position.y
    if (direction == Direction.UP) {
        if (y == 0) {
            return false
        }
        if (maze[y - 1][x] == 1) {
            return false
        }
    }
    if (direction == Direction.DOWN) {
        if (y == maze.size - 1) {
            return false
        }
        if (maze[y + 1][x] == 1) {
            return false
        }
    }
    if (direction == Direction.LEFT) {
        if (x == 0) {
            return false
        }
        if (maze[y][x - 1] == 1) {
            return false
        }
    }
    if (direction == Direction.RIGHT) {
        if (x == maze[0].size - 1) {
            return false
        }
        if (maze[y][x + 1] == 1) {
            return false
        }
    }
    return true
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT
}

fun printMaze(maze: List<List<Int>>, path: List<Cell>) {
//    var bottomAndTop = ""
//    // every block in the maze is represented by a symbol 3 characters long
//    // the side walls account for another 4 characters
//    for (i in 1..(maze[0].size * 3) + 4) {
//        bottomAndTop += "|"
//    }
//    println(bottomAndTop)
    for (row in maze.indices) {
//        print("||")
        for (column in maze[row].indices) {
            if (path.contains(Cell(column, row))) {
                print(" x ")
                continue
            }
            when (maze[row][column]) {
                0 -> print("   ")
                1 -> print("|||")
                2 -> print(" s ")
                3 -> print(" e ")
                4 -> print("|||")
            }
        }
//        println("||")
        println()
    }
//    print(bottomAndTop)
}