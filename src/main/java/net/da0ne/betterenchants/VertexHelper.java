package net.da0ne.betterenchants;

import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class VertexHelper {
    public static Vector3f[] getVertexPos(int[] vertexData)
    {
        int vertices = vertexData.length/8;
        Vector3f[] returnList = new Vector3f[vertices];
        for(int i =  0; i < vertices; i++)
        {
            int vertStride = (i*8);
            Vector3f vertPos = new Vector3f(Float.intBitsToFloat(vertexData[vertStride]),Float.intBitsToFloat(vertexData[vertStride+1]),Float.intBitsToFloat(vertexData[vertStride+2]));
            returnList[i] = vertPos;
        }
        return returnList;
    }

    public static void setVertexData(int[] outVertexData, Vector3f[] newPos)
    {
        int vertices = outVertexData.length/8;
        for(int i =  0; i < vertices; i++)
        {
            int vertStride = (i*8);
            outVertexData[vertStride] = Float.floatToIntBits(newPos[i].x);
            outVertexData[vertStride+1] = Float.floatToIntBits(newPos[i].y);
            outVertexData[vertStride+2] = Float.floatToIntBits(newPos[i].z);
        }
    }
}
