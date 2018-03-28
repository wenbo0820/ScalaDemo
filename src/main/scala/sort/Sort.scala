package sort

/**
  * Created by GongWenBo on 2018/1/25.
  */
trait Sort {
  def sort(array: Array[Int]): Unit
}

abstract class AbstractSort extends Sort {
  def swap(array: Array[Int], i: Int, j: Int) = {
    if (i != j) {
      array(i) = array(i) ^ array(j)
      array(j) = array(i) ^ array(j)
      array(i) = array(i) ^ array(j)
    }
  }
}

class BubbleSort extends AbstractSort {
  override def sort(array: Array[Int]): Unit = {
    for (i <- array.length - 1 until 0 by -1; j <- 0 until i if array(j) > array(j + 1))
      swap(array, j, j + 1)
  }
}

class SelectionSort extends AbstractSort {
  override def sort(array: Array[Int]): Unit = {
    var pos = 0
    for (i <- array.length - 1 until 0 by -1) {
      pos = 0
      for (j <- 0 to i if array(pos) < array(j))
        pos = j
      swap(array, pos, i)
    }
  }
}

class InsertionSort extends AbstractSort {
  override def sort(array: Array[Int]): Unit = {
    for (i <- 1 until array.length) {
      val temp = array(i)
      var j = i
      while (j > 0 && temp < array(j - 1)) {
        array(j) = array(j - 1)
        j -= 1
      }
      array(j) = temp
    }
  }

  def sort(list: List[Int]): List[Int] = {
    def insert(v: Int, vs: List[Int]): List[Int] = {
      vs match {
        case Nil => List(v)
        case y :: ys => if (v < y) v :: vs else y :: insert(v, ys)
      }
    }

    list match {
      case Nil => list
      case x :: xs => insert(x, sort(xs))
    }
  }

}

class ShellSort extends AbstractSort {
  override def sort(array: Array[Int]): Unit = {
    var step = array.length >> 1
    while (step > 0) {
      for (i <- step until array.length) {
        val temp = array(i)
        var j = i
        while (j >= step && array(j - step) > temp) {
          array(j) = array(j - step)
          j -= step
        }
        array(j) = temp
      }
      step >>= 1
    }
  }
}

class MergeSort extends AbstractSort {
  override def sort(array: Array[Int]): Unit = {
    def mSort(arr: Array[Int], left: Int, right: Int): Unit = {
      if (left < right) {
        val mid = (left + right) >> 1
        mSort(arr, left, mid)
        mSort(arr, mid + 1, right)
        merge(arr, left, mid, right)
      }
    }

    mSort(array, 0, array.length - 1)
  }

  def merge(arr: Array[Int], left: Int, mid: Int, right: Int) = {
    var b = left
    val temp = new Array[Int](right - left + 1)
    var (l, r) = (left, mid + 1)
    var index = 0
    while (l <= mid && r <= right)
      if (arr(l) < arr(r)) {
        temp(index) = arr(l)
        l += 1
        index += 1
      } else {
        temp(index) = arr(r)
        r += 1
        index += 1
      }
    while (l <= mid) {
      temp(index) = arr(l)
      l += 1
      index += 1
    }
    while (r <= right) {
      temp(index) = arr(r)
      r += 1
      index += 1
    }
    temp foreach {
      i =>
        arr(b) = i
        b += 1
    }
  }

  def sort(list: List[Int]): List[Int] = {
    def lmerge(left: List[Int], right: List[Int]): List[Int] = {
      (left, right) match {
        case (Nil, _) => right
        case (_, Nil) => left
        case (x :: xs, y :: ys) =>
          if (x < y) x :: lmerge(xs, right) else y :: lmerge(left, ys)
      }
    }

    val n = list.length >> 1
    if (n == 0) list
    else {
      val (left, right) = list.splitAt(n)
      lmerge(sort(left), sort(right))
    }
  }
}

class QuickSort extends AbstractSort {
  override def sort(array: Array[Int]): Unit = {
    sort(array, 0, array.length - 1)
  }

  private def sort(arr: Array[Int], left: Int, right: Int): Unit = {
    if (left >= right) return
    val temp = arr(left)
    var (l, r) = (left, right)
    while (l < r) {
      while (l < r && arr(r) >= temp) r -= 1
      while (l < r && arr(l) <= temp) l += 1
      if (l < r) {
        swap(arr, l, r)
        r -= 1
      }
    }
    swap(arr, left, l)
    sort(arr, left, l - 1)
    sort(arr, l + 1, right)

  }

  def sort(list: List[Int]): List[Int] = {
    list match {
      case Nil => list
      case (x :: xs) =>
        val (left, right) = xs.partition(_ < x)
        sort(left) ::: List(x) ::: sort(right)
    }
  }
}

class HeapSort extends AbstractSort {
  override def sort(array: Array[Int]): Unit = {
    buildHeap(array)
    for (i <- array.length - 1 to 0 by -1) {
      swap(array, 0, i)
      adjustHeap(array, 0, i)
    }
  }

  private def buildHeap(arr: Array[Int]): Unit = {
    for (i <- (arr.length >> 1) - 1 to 0 by -1)
      adjustHeap(arr, i, arr.length)
  }

  private def adjustHeap(arr: Array[Int], i: Int, length: Int): Unit = {
    var break = false
    var index = i
    var k = (i << 1) + 1
    while (k < length && !break) {
      if ((k + 1) < length && arr(k) < arr(k + 1)) k += 1
      if (arr(index) < arr(k)) {
        swap(arr, index, k)
        index = k
        k = (k << 1) + 1
      } else {
        break = true
      }
    }
  }
}

class BucketSort extends AbstractSort {
  override def sort(array: Array[Int]): Unit = {
    var d = 1
    val count = new Array[Int](10)
    val bucket = Array.ofDim[Int](10, array.length)
    for (_ <- 1 to 3) {
      array foreach {
        x =>
          val digit = (x / d) % 10
          bucket(digit)(count(digit)) = x
          count(digit) += 1
      }
      var k = 0
      for (b <- 0 until 10) {
        if (count(b) != 0) {
          for (w <- 0 until count(b)) {
            array(k) = bucket(b)(w)
            k += 1
          }
          count(b) = 0
        }
      }
      d *= 10
    }
  }
}


object Test extends App {

  val array = Array(8, 3, 2, 5, 6, 4, 1, 0, 7, 9, 32, 56, 12, 55)

  //  val sort = new BubbleSort
  //  val sort = new SelectionSort
  //  val sort = new InsertionSort
  //  val sort = new ShellSort
  //    val sort = new MergeSort
  //  val sort = new QuickSort
  //  val sort = new HeapSort
  //  val sort = new BucketSort

  //  sort.sort(array)
  //  array.foreach(x => print(x + " "))


  val list = List(5, 3, 2, 8, 1, 9, 32, 13, 87, 23, 11)
  val sortedList = new QuickSort sort list
  sortedList foreach (x => print(" " + x))
}