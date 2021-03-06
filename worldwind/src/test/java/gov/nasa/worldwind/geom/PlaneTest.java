/*
 * Copyright (c) 2016 United States Government as represented by the Administrator of the
 * National Aeronautics and Space Administration. All Rights Reserved.
 */

package gov.nasa.worldwind.geom;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import gov.nasa.worldwind.util.Logger;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class) // Support for mocking static methods
@PrepareForTest(Logger.class)   // We mock the Logger class to avoid its calls to android.util.log

public class PlaneTest {

    @Before
    public void setUp() throws Exception {
        // To accommodate WorldWind exception handling, we must mock all
        // the static methods in Logger to avoid calls to android.util.log
        PowerMockito.mockStatic(Logger.class);
    }

    @Test
    public void testConstructor_Doubles() {
        Vec3 n = new Vec3(3, 4, 5).normalize();

        Plane plane = new Plane(n.x, n.y, n.z, 10);

        assertNotNull(plane);
    }

    @Test
    public void testConstructor_Vector() {
        Vec3 normal = new Vec3(3, 4, 5).normalize();
        double distance = 6;

        Plane plane = new Plane(normal, distance);

        assertNotNull(plane);
    }

    @Test
    public void testConstructor_Copy() {
        Plane plane = new Plane(0, 0, 1, 10);

        Plane copy = new Plane(plane);

        assertNotNull("copy", copy);
        assertEquals("copy equal to original", plane, copy);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NullArgument() throws Exception {
        Plane copy = new Plane(null);

        fail("Expected an IllegalArgumentException to be thrown.");
    }

    @Test
    public void testEquals() throws Exception {
        Vec3 n = new Vec3(3, 4, 5).normalize();
        double distance = 6;

        Plane plane1 = new Plane(n.x, n.y, n.z, distance);
        Plane plane2 = new Plane(n.x, n.y, n.z, distance);

        assertEquals("normal", plane1.normal, plane2.normal);
        assertEquals("distance", plane1.distance, plane2.distance, 0);
        assertTrue("equals", plane1.equals(plane2));
    }

    @Test
    public void testEquals_Inequality() throws Exception {
        Vec3 n = new Vec3(3, 4, 5).normalize();
        double distance1 = 6;
        double distance2 = 7;

        Plane plane1 = new Plane(n.x, n.y, n.z, distance1);
        Plane plane2 = new Plane(n.x, n.y, n.z, distance2);
        Plane plane3 = new Plane(0, 1, 0, distance1);

        assertFalse("not equals", plane1.equals(plane2));
        assertFalse("not equals", plane1.equals(plane3));
    }

    @Test
    public void testEquals_Null() throws Exception {
        Vec3 n = new Vec3(3, 4, 5).normalize();
        double distance = 10;

        Plane plane1 = new Plane(n.x, n.y, n.z, distance);

        assertFalse("not equals", plane1.equals(null));
    }

    @Test
    public void testHashCode() throws Exception {
        Vec3 n = new Vec3(3, 4, 5).normalize();
        double distance1 = 6;
        double distance2 = 7;
        Plane plane1 = new Plane(n.x, n.y, n.z, distance1);
        Plane plane2 = new Plane(n.x, n.y, n.z, distance1);
        Plane plane3 = new Plane(n.x, n.y, n.z, distance2);

        int hashCode1 = plane1.hashCode();
        int hashCode2 = plane2.hashCode();
        int hashCode3 = plane3.hashCode();

        assertEquals(hashCode1, hashCode2);
        assertNotEquals(hashCode1, hashCode3);
    }

    @Test
    public void testToString() throws Exception {
        Vec3 n = new Vec3(3, 4, 5).normalize();
        double distance = 6;
        Plane plane = new Plane(n.x, n.y, n.z, distance);

        String string = plane.toString();

        assertTrue("normal x", string.contains(Double.toString(plane.normal.x)));
        assertTrue("normal y", string.contains(Double.toString(plane.normal.y)));
        assertTrue("normal z", string.contains(Double.toString(plane.normal.z)));
        assertTrue("distance", string.contains(Double.toString(plane.distance)));
    }

    @Test
    public void testDistanceToPoint() throws Exception {
        Vec3 normal = new Vec3(3, 4, 5).normalize();// arbitrary orientation
        double distance = 10;                       // arbitrary distance
        Plane plane = new Plane(normal, distance);
        // The plane's normal points towards the origin, so use the normal's
        // reversed direction to create a point on the plane
        Vec3 point = new Vec3(normal).negate().multiply(distance);
        Vec3 origin = new Vec3(0, 0, 0);

        double distanceToOrigin = plane.distanceToPoint(origin);
        double distanceToPoint = plane.distanceToPoint(point);

        assertEquals("distance to origin", distance, distanceToOrigin, 0);
        assertEquals("distance to point on plane", 0, distanceToPoint, 0);
    }


    @Test
    public void testSet() throws Exception {
        Vec3 n = new Vec3(3, 4, 5).normalize();
        double distance = 6;
        Plane plane = new Plane(0, 0, 1, 10);

        plane.set(n.x, n.y, n.z, distance);

        assertEquals("normal", n, plane.normal);
        assertEquals("distance", distance, plane.distance, 0);
    }

    @Test
    public void testSet_Vector() throws Exception {
        Vec3 n = new Vec3(3, 4, 5).normalize();
        double distance = 6;
        Plane plane = new Plane(0, 0, 1, 10);

        plane.set(n, distance);

        assertEquals("normal", n, plane.normal);
        assertEquals("distance", distance, plane.distance, 0);
    }

    @Test
    public void testSet_Plane() throws Exception {
        Vec3 n = new Vec3(3, 4, 5).normalize();
        double distance = 6;
        Plane plane1 = new Plane(0, 0, 1, 10);
        Plane plane2 = new Plane(n, distance);

        plane1.set(plane2);

        assertEquals("normal", n, plane1.normal);
        assertEquals("distance", distance, plane1.distance, 0);
    }

    @Test
    public void testTransformByMatrix() throws Exception {
        Plane p = new Plane(new Vec3(0, 0, -1), 10);
        // An arbitrary transformation matrix
        double theta = 30d;
        double x = 0;
        double y = 0;
        double z = 5;
        Matrix4 m = new Matrix4().multiplyByRotation(1, 0, 0, theta).setTranslation(x, y, z);
        System.out.println(p);

        p.transformByMatrix(m);
        System.out.println(p);

        p.normalize();
        System.out.println(p);

        fail("Shouldn't the output of transformByMatrix be normalized?");
    }

    @Test
    public void testNormalize() throws Exception {
        Vec3 u = new Vec3(3, 4, 5);
        double distance = 5;
        Plane plane = new Plane(u, distance);

        plane.normalize();

        assertEquals("Normal magnitude", 1.0, plane.normal.magnitude(), 1e-10);
        assertEquals("distance", distance / u.magnitude(), plane.distance, 1e-10);
    }

    @Test
    public void testDot() throws Exception {
        double distance = 6;
        Vec3 n = new Vec3(3, 4, 5).normalize();
        Vec3 u = new Vec3(7, 8, 9);
        Plane plane = new Plane(n.x, n.y, n.z, distance);
        double expected = n.dot(u) + distance;

        double result = plane.dot(u);

        assertEquals("plane dot product", expected, result, 0);
    }

    @Test
    public void testIntersectsSegment() throws Exception {
        Plane p = new Plane(new Vec3(0, 0, -1), 0);
        boolean result = false;

        // These tests were adapted from WorldWindJava PlaneTest
        result = p.intersectsSegment(new Vec3(), new Vec3(0, 0, -1));
        assertTrue("Perpendicular, 0 at origin, should produce intersection at origin", result);

        result = p.intersectsSegment(new Vec3(1, 0, 0), new Vec3(1, 0, 0));
        assertTrue("Line segment is in fact a point, located on the plane, should produce intersection at (1, 0, 0)", result);

        result = p.intersectsSegment(new Vec3(0, 0, -1), new Vec3(0, 0, -1));
        assertFalse("Line segment is in fact a point not on the plane, should produce no intersection", result);

        result = p.intersectsSegment(new Vec3(0, 0, 1), new Vec3(0, 0, -1));
        assertTrue("Perpendicular, integer end points off origin, should produce intersection at origin", result);

        result = p.intersectsSegment(new Vec3(0, 0, 0.5), new Vec3(0, 0, -0.5));
        assertTrue("Perpendicular, non-integer end points off origin, should produce intersection at origin", result);

        result = p.intersectsSegment(new Vec3(0.5, 0.5, 0.5), new Vec3(-0.5, -0.5, -0.5));
        assertTrue("Not perpendicular, non-integer end points off origin, should produce intersection at origin", result);

        result = p.intersectsSegment(new Vec3(1, 0, 0), new Vec3(2, 0, 0));
        assertTrue("Parallel, in plane, should produce intersection at origin", result);

        result = p.intersectsSegment(new Vec3(1, 0, 1), new Vec3(2, 0, 1));
        assertFalse("Parallel, integer end points off origin, should produce no intersection", result);

    }

    @Test
    public void testOnSameSide() throws Exception {
        Plane p = new Plane(new Vec3(0, 0, -1), 0); // a plane at the origin
        int result = 0;

        result = p.onSameSide(new Vec3(1, 2, -1), new Vec3(3, 4, -1));
        assertEquals("Different points on positive side of the plane (with respect to normal vector)", 1, result);

        result = p.onSameSide(new Vec3(1, 2, 1), new Vec3(3, 4, 1));
        assertEquals("Different points on negative side of the plane (with respect to normal vector)", -1, result);

        result = p.onSameSide(new Vec3(1, 2, 0), new Vec3(3, 4, -1));
        assertEquals("One point located on the plane, the other on the positive side the plane", 0, result);

        result = p.onSameSide(new Vec3(1, 2, 0), new Vec3(3, 4, 1));
        assertEquals("One point located on the plane, the other on the negative side the plane", 0, result);

        result = p.onSameSide(new Vec3(1, 0, 0), new Vec3(1, 0, 0));
        assertEquals("Coincident points, located on the plane", 0, result);

        result = p.onSameSide(new Vec3(1, 2, 0), new Vec3(3, 4, 0));
        assertEquals("Different points located on the plane", 0, result);

        result = p.onSameSide(new Vec3(1, 2, 1), new Vec3(3, 4, -1));
        assertEquals("Different points on opposite sides of the plane", 0, result);
    }

    @Test
    public void testClip() throws Exception {
        Plane p = new Plane(new Vec3(0, 0, -1), 0); // a plane at the origin
        Vec3[] result;
        Vec3 a = new Vec3(1, 2, 0);
        Vec3 b = new Vec3(3, 4, 0);

        // If the segment is coincident with the plane, the input points are returned, in their input order.
        result = p.clip(a, b);

        assertNotNull("Segment coincident with plane", result);
        assertEquals("Coincident segment, start point unchanged", result[0], a);
        assertEquals("Coincident segment, end point unchanged", result[1], b);
    }

    @Test
    public void testClip_NonIntersecting() throws Exception {
        Plane p = new Plane(new Vec3(0, 0, -1), 0); // a plane at the origin
        Vec3[] result;
        Vec3 a = new Vec3(1, 2, -1);
        Vec3 b = new Vec3(3, 4, -1);

        // If the segment does not intersect the plane, null is returned.
        result = p.clip(a, b);

        assertNull("Non-intersecting points", result);

    }

    @Test
    public void testClip_PositiveDirection() throws Exception {
        // If the direction of the line formed by the two points is positive with respect to this plane's normal vector,
        // the first point in the array will be the intersection point on the plane, and the second point will be the
        // original segment end point.
        Plane p = new Plane(new Vec3(0, 0, -1), 0); // a plane at the origin
        Vec3[] result;
        Vec3 a = new Vec3(1, 2, 1);
        Vec3 b = new Vec3(3, 4, -1);
        Vec3 expected0 = new Vec3(2, 3, 0);
        Vec3 expected1 = new Vec3(b);

        result = p.clip(a, b);

        assertNotNull("Positive direction with respect normal, intersecting the plane", result);
        assertEquals("Positive direction, the start point is the segment's original begin point", expected0, result[0]);
        assertEquals("Positive direction, the end point is the segment's intersection with the plane", expected1, result[1]);
    }

    @Test
    public void testClip_NegativeDirection() throws Exception {
        // If the direction of the line is negative with respect to this plane's normal vector, the first point in the
        // array will be the original segment's begin point, and the second point will be the intersection point on the
        // plane.
        Plane p = new Plane(new Vec3(0, 0, -1), 0); // a plane at the origin
        Vec3[] result;
        Vec3 a = new Vec3(1, 2, -1);
        Vec3 b = new Vec3(3, 4, 1);
        Vec3 expected0 = new Vec3(a);
        Vec3 expected1 = new Vec3(2, 3, 0);

        result = p.clip(a, b);

        assertNotNull("Negative direction with respect normal, intersecting the plane", result);
        assertEquals("Negative direction, the start point is the segment's original begin point", expected0, result[0]);
        assertEquals("Negative direction, the end point is the segment's intersection with the plane", expected1, result[1]);

    }


}