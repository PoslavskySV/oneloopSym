package cc.redberry.onloopsym

import cc.redberry.core.indexgenerator.IndexGeneratorImpl
import cc.redberry.core.indices.IndicesFactory
import cc.redberry.core.indices.SimpleIndices
import cc.redberry.core.tensor.SimpleTensor
import cc.redberry.core.tensor.Tensor
import cc.redberry.core.utils.TensorUtils
import cc.redberry.groovy.Redberry

import static cc.redberry.core.tensor.Tensors.simpleTensor
import static cc.redberry.groovy.RedberryStatic.ExpandAndEliminate
import static cc.redberry.groovy.RedberryStatic.Symmetrize

class SymmetrizeOp {

    static def covariantCommutator(SimpleIndices dIndices, Tensor expression) {
        use(Redberry) {
            def upper = expression.indices.upper.free
            def lower = expression.indices.lower.free

            def forbiddenIndices = TensorUtils.getAllIndicesNamesT(expression)
            forbiddenIndices.addAll(dIndices.toArray())
            def ig = new IndexGeneratorImpl(forbiddenIndices.toArray())
            def dummyIndex = IndicesFactory.create(ig.generate(dIndices.get(0).type)).si

            def result = 0.t
            for (int k = 0; k < upper.size(); ++k) {
                def currentIndex = upper[k..k].si
                def riemannIndices = currentIndex + dummyIndex + dIndices
                result += simpleTensor("R", riemannIndices) * ((currentIndex % dummyIndex.inverted) >> expression)
            }

            for (int k = 0; k < lower.size(); ++k) {
                def currentIndex = lower[k..k].si
                def riemannIndices = dummyIndex.inverted + currentIndex + dIndices
                result -= simpleTensor("R", riemannIndices) * ((currentIndex % dummyIndex) >> expression)
            }

            return result
        }
    }

    static def symmetrizePair(Tensor gTensor, SimpleTensor nabla, Tensor hTensor, int iPosition) {
        use(Redberry) {
            def product = gTensor * nabla * hTensor
//        if (product.indices.free.size() != 0)
//            throw new IllegalArgumentException("bad input: $gTensor * $nabla * $hTensor")

            def toSymmetrize = nabla.indices[iPosition, iPosition + 1].inverted
            toSymmetrize.symmetries.setSymmetric()
            def higher = (Symmetrize[toSymmetrize] >> gTensor) * nabla * hTensor


            def lNabla = iPosition == 0 ? 1.t : simpleTensor(nabla.stringName, nabla.indices[0..(iPosition - 1)])
            def rNabla = iPosition == nabla.indices.size() - 2 ? 1.t : simpleTensor(nabla.stringName, nabla.indices[(iPosition + 2)..(nabla.indices.size() - 1)])
            def nNabla = ((lNabla.indices + rNabla.indices).size() == 0) ? 1.t : simpleTensor(nabla.stringName, lNabla.indices + rNabla.indices)
            def subs = (lNabla * rNabla).eq(nNabla)

            def lower = covariantCommutator(nabla.indices[iPosition, iPosition + 1], rNabla * hTensor) * lNabla * gTensor / 2.t
            lower <<= ExpandAndEliminate & subs

            return higher + lower
        }
    }


}
