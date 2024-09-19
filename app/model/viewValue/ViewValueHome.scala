package model.viewValue

import model.viewValue.common.ViewValueCommon

// Topページのviewvalue
case class ViewValueHome(
  title:  String,
  cssSrc: Seq[String],
  jsSrc:  Seq[String],
) extends ViewValueCommon
