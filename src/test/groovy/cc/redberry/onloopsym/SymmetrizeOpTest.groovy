package cc.redberry.onloopsym

import cc.redberry.core.utils.TensorUtils
import cc.redberry.groovy.Redberry
import org.junit.Test

import static cc.redberry.core.tensor.Tensors.addSymmetries
import static cc.redberry.core.tensor.Tensors.setSymmetric
import static cc.redberry.groovy.RedberryStatic.*
import static cc.redberry.onloopsym.SymmetrizeOp.symmetrizePair

/**
 *
 * @author Stanislav Poslavsky
 * @since 1.0
 */
class SymmetrizeOpTest {

    @Test
    void test1() {
        use(Redberry) {
            addSymmetries 'R_abcd', -[[0, 1]].p, [[0, 2], [1, 3]].p
            setSymmetric 'R_ab'
            setSymmetric 'H_ab'

            def gTensor = 'G^ab_ij'.t
            def nabla = 'N_ab'.t
            def hTensor = 'H^ij'.t

//    symmetrizePair(gTensor, nabla, hTensor, 0)
//    symmetrizePair(gTensor, nabla, hTensor, 1)
//    symmetrizePair(gTensor, nabla, hTensor, 2)
//    println(gTensor.indices[0..1])
//    println(symmetrizePair(gTensor, nabla, hTensor, 1))
//    println(gTensor.indices[2])

//    println(covariantCommutator('_mn'.si, "T^a_b".t))

            'd_abcd := (1/2)*(d_ab*d_cd + d_ac*d_bd)'.t

            def op = 'd_ab^gd * N_a^a + (1/2)*(d_a^g * N_b^d + d_b^g * N_a^d + d_a^d * N_b^g + d_b^d * N_a^g)'.t
            def k = 'K_c^e_{ab}^{gd} = ((1/2)*d_{a}^{e}*d_{b}^{g}*d^{d}_{c}+(1/2)*d^{g}_{c}*d_{a}^{d}*d_{b}^{e}+(1/2)*d_{a}^{g}*d^{d}_{c}*d_{b}^{e}+(1/2)*d_{a}^{e}*d^{g}_{c}*d_{b}^{d}+d^{e}_{c}*d_{ab}^{gd})'.t
//    println(Collect['N_ab'] >> op)

            def symmetrized = symmetrizePair('K^ab_{ijpq}'.t, 'N_ab'.t, 'H^ij'.t, 0) << k
            symmetrized <<= ExpandAndEliminate
            symmetrized <<= 'R^a_bac = R_bc'.t
            symmetrized <<= 'R^a_a = R'.t


            symmetrized <<= Collect['N_ab'.t, 'H^ij'.t] & EliminateDueSymmetries

            println TensorUtils.info(symmetrized)

            println(symmetrized[0])
            println(symmetrized[1])
        }
    }
}
