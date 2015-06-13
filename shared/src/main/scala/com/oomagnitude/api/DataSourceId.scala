package com.oomagnitude.api

case class DataSourceId(experiment: String, date: String, name: String) {
  override def toString: String = s"$experiment/$date/$name"
}