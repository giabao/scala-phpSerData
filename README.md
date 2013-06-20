scala-phpSerData
================

Scala library for interfacing with php serialized format (data only - no objects)

## Usage
```scala
import si.bss.tools.phpSerData._
```


## Parsing
```scala
val phpSerializedContent = """a:2:{s:3:"one";d:5.0;s:3:"two";d:6.0;}"""
val parsed = PHPVal.parse(phpSerializedContent)

if (parsed.successful) {
  val phpValue: PHPValue = parsed.get
  val prettyPrintedString = PHPVal.prettyPrint(phpValue)
  
  println(prettyPrintedString)
  /* will print:
  PHPArray {
    PHPString("one") => PHPDouble(5.0),
    PHPString("two") => PHPDouble(6.0)
  }
  */
}
```

## Serializing
```scala
val phpValue = PHPArray(
                  List(
                    (PHPString("one") -> PHPDouble(5)), 
                    (PHPString("two") -> PHPDouble(6)) 
                  )
              )
              
val serialized = PHPVal.stringify(phpValue)
println(serialized)
/* will print:
a:2:{s:3:"one";d:5.0;s:3:"two";d:6.0;}
*/
```

## Reading to Scala types

This is similar to JSON Reads in Play framework.
```scala
// The same thing as above
val phpSerializedContent = """a:2:{s:3:"one";d:5.0;s:3:"two";d:6.0;}"""
val parsed = PHPVal.parse(phpSerializedContent)

if (parsed.successful) {
  val phpValue: PHPValue = parsed.get
  
  //but now we want to get a map out of it
  val assocMap: Map[String,Double] = PHPVal.fromPHPVal[Map[String,Double]](phpValue)
}
```

You can also define Reads for your own type
```scala
//Your ususal case class
case class ReadsTest(a: Int, b: String)

//Implement the Reads[A] for your type
implicit val myTypeReads = new Reads[ReadsTest] {
  def reads(phpValue: PHPValue): Try[ReadsTest] = {
    phpValue match {
      case a: PHPArray => {
        val atribs = a.a.toMap
        val atribAT = Try(atribs(PHPString("a")))
        val atribBT = Try(atribs(PHPString("b")))
        for {
          atribA <- atribAT
          atribB <- atribBT
          atrA <- PHPVal.fromPHPVal[Int](atribA)
          atrB <- PHPVal.fromPHPVal[String](atribB)
        } yield ReadsTest(atrA,atrB)
      }
      case _ => Failure(new Exception("Not an array, can not parse my type out"))
    }
  }
}

//You get this from the parsing step
val parsedValue = PHPArray(List(
  (PHPString("a") -> PHPInt(123)),
  (PHPString("b") -> PHPString("onetwothree"))
))

//And voila:
val read = PHPVal.fromPHPVal[ReadsTest](parsedValue)
```



