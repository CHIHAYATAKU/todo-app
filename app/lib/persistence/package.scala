/**
  * This is a sample of Todo Application.
  * 
  */

package lib

import slick.jdbc.MySQLProfile

package object persistence {

  val default = onMySQL
  
  object onMySQL {
    implicit lazy val driver: MySQLProfile = slick.jdbc.MySQLProfile
  }
}
