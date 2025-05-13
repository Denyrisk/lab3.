import kotlinx.coroutines.*

class Student private constructor(private var _name: String) {

    private var _age: Int = 0
    private var _grades: List<Int> = listOf()

    constructor(name: String, age: Int, grades: List<Int>) : this(name) {
        this.age = age
        this.grades = grades
    }

    init {
        println("Student object created: $name")
    }

    var name: String
        get() = _name
        set(value) {
            _name = value.trim().replaceFirstChar { it.uppercaseChar() }
        }

    var age: Int
        get() = _age
        set(value) {
            if (value >= 0) _age = value
        }

    var grades: List<Int>
        get() = _grades
        private set(value) {
            _grades = value
        }

    val isAdult: Boolean
        get() = _age >= 18

    val status: String by lazy {
        if (isAdult) "Adult" else "Minor"
    }

    fun getAverage(): Double = _grades.average()

    fun processGrades(operation: (Int) -> Int) {
        _grades = _grades.map(operation)
    }

    fun updateGrades(grades: List<Int>) {
        this.grades = grades
    }

    operator fun plus(other: Student): Student {
        return Student(this.name, this.age, this._grades + other._grades)
    }

    operator fun times(multiplier: Int): Student {
        return Student(this.name, this.age, this._grades.map { it * multiplier })
    }

    override operator fun equals(other: Any?): Boolean {
        return other is Student && this.name == other.name &&
               this.getAverage() == other.getAverage()
    }
}

class Group(vararg students: Student) {
    private val studentList = students.toList()

    operator fun get(index: Int): Student = studentList[index]

    fun getTopStudent(): Student = studentList.maxByOrNull { it.getAverage() }!!
}

suspend fun fetchGradesFromServer(): List<Int> {
    delay(2000)
    return listOf(95, 87, 74, 88, 90)
}

fun main() = runBlocking {
    val student1 = Student("  alice  ")
    student1.age = 19
    println("Is adult: ${student1.isAdult}, Status: ${student1.status}")

    val deferredGrades = async { fetchGradesFromServer() }
    val newGrades = deferredGrades.await()
    student1.updateGrades(newGrades)

    val student2 = Student(name = "Bob", age = 20, grades = listOf(60, 70, 80))
    val student3 = student1 + student2
    val boostedStudent = student2 * 2

    val group = Group(student1, student2, boostedStudent)
    println("Top student: ${group.getTopStudent().name}")

    println("Student1 grades: ${student1.grades}")
    student1.processGrades { it + 5 }
    println("Student1 grades after process: ${student1.grades}")
}
