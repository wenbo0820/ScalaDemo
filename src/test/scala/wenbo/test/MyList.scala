package wenbo.test

/**
  * Created by GongWenBo on 2018/2/8.
  */
class Friend[-T]

class Person

class Student extends Person

object Demo extends App {

  def test(friend: Friend[Student]) = Unit

  test(new Friend[Student])
  test(new Friend[Person])

  val func = (_: Friend[Student]) => new Student
  val func2: Friend[Student] => Student = {
    _ => new Student
  }

  val p: Student = func(new Friend[Person])
  val p2: Person = func2(new Friend[Person])
}