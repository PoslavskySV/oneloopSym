package cc.redberry.onloopsym

import cc.redberry.core.context.CC
import cc.redberry.core.utils.TensorUtils
import cc.redberry.groovy.Redberry
import org.junit.Test

import static cc.redberry.core.tensor.Tensors.addSymmetries
import static cc.redberry.core.tensor.Tensors.setSymmetric
import static cc.redberry.groovy.RedberryStatic.*
import static cc.redberry.onloopsym.SymmetrizeOp.symmetrizePair

//Проверяем вычисления для работы "Quantum properties of affine-metric gravity with the cosmological term".
//Создаём класс SymmetrizeOpTest
class SymmetrizeOpTest {

    @Test // ЭТО ЧТО ???
    void test1() { //Ключевое слово void указывает на то, что метод ничего не возвращает. Условно методы,
        // которые не возвращают никакого значения, называются процедурами.
        use(Redberry) {//указываем, что будем использовать редберри
            CC.resetTensorNames(1) //фиксирует порядок слагаемых в выводимом результате
            //Устанавливаем симметрии тензора Римана. Метод addSymmetry имеет два аргумента: простой тензор
            // (или его строковое представление) и перестановку. Redberry имеет внутреннее представление
            // перестановок и групп перестановок. Чтобы преобразовать массив в перестановку, можно
            // использовать свойство .p, которое следует за массивом, записанным в однострочном
            // или непересекающемся цикле. Минус (используемый в последней строке) преобразует
            // симметрию в антисимметрию и наоборот.

            // ПОЧЕМУ У НАС addSymmetries, А НЕ addSymmetry ? ПОТОМУ ЧТО ДОБАВЛЯЕМ ДВЕ СИММЕТРИИ?

            addSymmetries 'R_abcd', -[[0, 1]].p, [[0, 2], [1, 3]].p

            //Устанавливаем симметрии тензоров R и H.
            setSymmetric 'R_ab'
            setSymmetric 'H_ab'

            // Определяем переменные (были в методе symmetrizePair класса SymmetrizeOp)
            //.t превращает обычные символы в компьютерный объект
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
            // Определяем четырёхмерную единицу
            'd_abcd := (1/2)*(d_ab*d_cd + d_ac*d_bd)'.t
            // Вводим оператор, соответствующий D2. Почему "op" - серое? Куда оно ссылается?
//            def op = 'd_ab^gd * N_a^a + (1/2)*(d_a^g * N_b^d + d_b^g * N_a^d + d_a^d * N_b^g + d_b^d * N_a^g)'.t
            // Вводим матрицу К, соответствующую оператору D2.
            def k = 'K_c^e_{ab}^{gd} = ((1/2)*d_{a}^{e}*d_{b}^{g}*d^{d}_{c}+(1/2)*d^{g}_{c}*d_{a}^{d}*d_{b}^{e}+(1/2)*d_{a}^{g}*d^{d}_{c}*d_{b}^{e}+(1/2)*d_{a}^{e}*d^{g}_{c}*d_{b}^{d}+d^{e}_{c}*d_{ab}^{gd})'.t
//    println(Collect['N_ab'] >> op)
            // << (Бинарный оператор сдвига влево. Значение левых операндов перемещается влево на количество бит,
            // заданных правым операндом.)

            // ЧТО ПРОИСХОДИТ ?

            //Создаём переменную symmetrized и ... ??
            def symmetrized = symmetrizePair('K^ab_{ijpq}'.t, 'N_ab'.t, 'H^ij'.t, 0)
            symmetrized <<= k

            // <<= (Оператор присваивания «Сдвиг влево», C << = 2, это как C = C << 2)
            //ExpandAndEliminate разлагает произведение сумм и положительных целых степеней и,
            // по ходу дела, устраняет метрические тензоры и дельты Кронекера.
            symmetrized <<= ExpandAndEliminate
            // Тензор Риччи
            symmetrized <<= 'R^a_bac = R_bc'.t
            // Скалярная кривизна
            symmetrized <<= 'R^a_a = R'.t

            //Collect[var1, var2] собирает вместе члены, имеющие те же степени, что и указанные выражения.
            // В случае тензорных выражений Collect введёт дельты Кронекера или метрические тензоры, чтобы
            // «???» индексы и разложить тензорные части.
            //В случае скалярных выражений поведение Collect аналогично другим CAS.
            //EliminateDueSymmetries удаляет части выражения, которые равны нулю из-за симметрии.
            symmetrized <<= Collect['N_ab'.t, 'H^ij'.t] & EliminateDueSymmetries
            //Статический метод info из класса TensorUtils с параметром symmetrized возвращает информацию
            // об объекте symmetrized.
            println(TensorUtils.info(symmetrized))
            //Произведение матрицы W и поля H: W^{a}_{p,q,c}*H^{c}_{a} !НЕ СОВПАДАЕТ!
            println(symmetrized[0])
            // Что он выводит тут ?
            println(symmetrized[1])
        }
    }
}
