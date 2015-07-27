package d3.facade.scale

import scalajs.js

trait Scales extends js.Object {
//  def linear[D, R]: LinearScale[D, R]
  def ordinal(): OrdinalScale
//  def identity[D]: IdentityScale[D]
//  def log[D, R]: LogScale[D, R]
//  def time[D, R]: TimeScale[D, R]
//  def timeUtc[D, R]: TimeScale[D, R]
//  def threshold[D, R]: ThresholdScale[D, R]
//  def quantize[D, R]: QuantizeScale[D, R]
//  def quantile[D, R]: QuantileScale[D, R]
//  def power: PowerScale[Double, Double]
//  def squareRoot: PowerScale[Double, Double] = power.exponent(0.5)
  def category10(): OrdinalScale
  def category20(): OrdinalScale
  def category20b(): OrdinalScale
  def category20c(): OrdinalScale
}