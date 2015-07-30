package org.briljantframework.array.base

import spock.lang.Specification

/**
 * Created by isak on 01/06/15.
 */
class BaseArrayRoutinesSpec extends Specification {

    static bj = new BaseArrayFactory()
    static bjr = new BaseArrayRoutines()

    def "mean of double matrices should return the mean"() {
        expect:
        bjr.mean(a) == b

        where:
        a << [
                bj.array([1, 2, 3, 4, 5, 6] as double[]),
                bj.array([-1, -2, -3, -4, -5, -6] as double[])
        ]
        b << [3.5, -3.5]
    }




}
