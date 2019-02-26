package cc.redberry.onloopsym

import cc.redberry.core.context.CC
import cc.redberry.core.utils.TensorUtils
import cc.redberry.groovy.Redberry
import org.junit.Test

import static cc.redberry.core.context.OutputFormat.*
import static cc.redberry.core.tensor.Tensors.addSymmetries
import static cc.redberry.core.tensor.Tensors.setSymmetric
import static cc.redberry.groovy.RedberryStatic.*
import static cc.redberry.onloopsym.SymmetrizeOp.symmetrizePair
import static cc.redberry.onloopsym.SymmetrizeOp.symmetrizeAll

//Проверяем вычисления для работы "Quantum properties of affine-metric gravity with the cosmological term".
//Создаём класс SymmetrizeOpTest
class SymmetrizeOpTest {
//Тест выражения для матрицы W оператора D2, уже проведена свёртка с ков. производными и полем h.
    //P_qp означает часть выражения, взятую непосредственно из вывода программы в test1, и соответствующую вкладу W
    //H_ab означает ту же часть, но полученную на бумаге
    //test_op2_correctness сравнивает эти две части
    @Test
    void test_op2_correctness() {
        use(Redberry) {
            addSymmetries 'R_abcd', -[[0, 1]].p, [[0, 2], [1, 3]].p
            setSymmetric 'h_ab'

            'R_ab := R^c_acb'.t

            CC.parserAllowsSameVariance = true
            def proga = 'P_qp = (-1/2*g_pc*R_aq - 1/2*g_qc*R_ap + R_qapc)*h_ca'.t
            def byhand = 'H_ab = (1/2)*(-R_bn*h_an - R_an*h_bn + R_bdag*h_gd + R_adbg*h_gd)'.t
            println((proga & byhand & ExpandAndEliminate) >> 'P_qp - H_qp'.t)
        }
    }
//Тест выражения для оператора D2, где уже проведена свёртка с ков. производными и полем h.
    //actual означает вывод программы в test1, целиком
    //expected означает то же выражение, но полученное на бумаге
    @Test
    void test1_operator2() {
        use(Redberry) {
            addSymmetries 'R_abcd', -[[0, 1]].p, [[0, 2], [1, 3]].p
            setSymmetric 'h_ab'
            'R_ab := R^c_acb'.t

            CC.parserAllowsSameVariance = true

            'd_abcd := (1/2)*(d_ab*d_cd + d_ac*d_bd)'.t
            def kTensors = 'K_c^e_{ab}^{gd} = ((1/2)*d_{a}^{e}*d_{b}^{g}*d^{d}_{c}+(1/2)*d^{g}_{c}*d_{a}^{d}*d_{b}^{e}+(1/2)*d_{a}^{g}*d^{d}_{c}*d_{b}^{e}+(1/2)*d_{a}^{e}*d^{g}_{c}*d_{b}^{d}+d^{e}_{c}*d_{ab}^{gd})'.t
            def actual = symmetrizePair('K^ab_{ijpq}'.t, 'N_ab'.t, 'H^ij'.t, 0)
            actual <<= kTensors & ExpandAndEliminate & 'd_a^a = 4'.t

            //не правильно записан expected? ВЫДАЁТ ОШИБКУ!
            def expected = 'N_ab*(d_{mnpq}*g^{ab}+(1/2)*(g_{mp}*d_{nq}^{ab}+g_{np}*d_{mq}^{ab}+g_{mq}*d_{np}^{ab}+g_{nq}*d_{mp}^{ab}))*h^{pq}+(1/2)*(R_{nqmp}*h^{pq}+R_{mqnp}*h^{pq}-R_{nb}*h^{b}_{m}-R_{mb}*h^{b}_{n})'.t

            assert TensorUtils.equals(expected, actual)
        }
    }

//Вычисляем выражение для матрицы W оператора D2
    @Test // ЭТО ЧТО ???
    // это аннотация (https://en.wikipedia.org/wiki/Java_annotation) --- штука аля декораторов в python или аттрибутов в C#
    // в данном случае это аннотация из пакета JUnit (junit.org) -- говорит о том, что данный метод 
    // является тестом; с помощью JUnit можно тестовые методы запускать не создавая Main-класса (JUnit 
    // автоматически создаст нужный main-класс и тп); короче здесь эта тема для тестирования и чтобы не 
    // писать каждый раз новый main-класс чтобы что-то попырить поиграться
    void test1() { //Ключевое слово void указывает на то, что метод ничего не возвращает. Условно методы,
        // которые не возвращают никакого значения, называются процедурами.
        use(Redberry) {//указываем, что будем использовать редберри <--- //указываем, что будем использовать синтаксис определенный в классе Redberry
            CC.resetTensorNames(1) //фиксирует порядок слагаемых в выводимом результате
            //Устанавливаем симметрии тензора Римана. Метод addSymmetry имеет два аргумента: простой тензор
            // (или его строковое представление) и перестановку. Redberry имеет внутреннее представление
            // перестановок и групп перестановок. Чтобы преобразовать массив в перестановку, можно
            // использовать свойство .p, которое следует за массивом, записанным в однострочном
            // или непересекающемся цикле. Минус (используемый в последней строке) преобразует
            // симметрию в антисимметрию и наоборот.

            // ПОЧЕМУ У НАС addSymmetries, А НЕ addSymmetry ? ПОТОМУ ЧТО ДОБАВЛЯЕМ ДВЕ СИММЕТРИИ?
            // Да, тк добавляем две симметии (можно так сколько угодно добавлять через запятую)

            addSymmetries 'R_abcd', -[[0, 1]].p, [[0, 2], [1, 3]].p

            //Устанавливаем симметрии тензоров R и H.
            setSymmetric 'R_ab'
            setSymmetric 'H_ab'

            // Определяем переменные (были в методе symmetrizePair класса SymmetrizeOp)
            //.t превращает обычные символы в компьютерный объект
            //def gTensor = 'G^ab_ij'.t
            //def nabla = 'N_ab'.t
            //def hTensor = 'H^ij'.t

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
            // Вводим матрицу К, соответствующую оператору D2, т.е. ещё не симметризованную.
            def k = 'K_c^e_{ab}^{gd} = ((1/2)*d_{a}^{e}*d_{b}^{g}*d^{d}_{c}+(1/2)*d^{g}_{c}*d_{a}^{d}*d_{b}^{e}+(1/2)*d_{a}^{g}*d^{d}_{c}*d_{b}^{e}+(1/2)*d_{a}^{e}*d^{g}_{c}*d_{b}^{d}+d^{e}_{c}*d_{ab}^{gd})'.t
//    println(Collect['N_ab'] >> op)
            // << (Бинарный оператор сдвига влево. Значение левых операндов перемещается влево на количество бит,
            // заданных правым операндом.)
            // в данном случае оператор "<<" переопределен (см. перегрузка операторов на википедии)

            // ЧТО ПРОИСХОДИТ ?

            //Создаём переменную symmetrized и ... ?? 
            // и присваеваем ей значение, которое возвращает метод symmetrizePair (он в другом фале определен,
            // мы его импортировали через "import static cc.redberry.onloopsym.SymmetrizeOp.symmetrizePair")
            // symmetrizePair берет 3 аргумента и возвращает в этом направлении некий тензор
            //здесь a,b - индексы, сворачиваемые с двумя ковариантными производными, именно по ним проводится симметризация
            def symmetrized = symmetrizePair('K^ab_{ijpq}'.t, 'N_ab'.t, 'H^ij'.t, 0)
            symmetrized <<= k

            // <<= (Оператор присваивания «Сдвиг влево», C << = 2, это как C = C << 2)
            //ExpandAndEliminate разлагает произведение сумм и положительных целых степеней и,
            // по ходу дела, устраняет метрические тензоры и дельты Кронекера.
            symmetrized <<= ExpandAndEliminate
            // подставляем сокращение для Тензора Риччи
            symmetrized <<= 'R^a_bac = R_bc'.t
            // подставляем сокращение для скалярной кривизны
            symmetrized <<= 'R^a_a = R'.t

            //Collect[var1, var2] собирает вместе члены, имеющие те же степени, что и указанные выражения.
            // В случае тензорных выражений Collect введёт дельты Кронекера или метрические тензоры, чтобы
            // «???» индексы и разложить тензорные части. (см примеры http://redberry.cc/documentation:ref:collect#collect)
            //В случае скалярных выражений поведение Collect аналогично другим CAS.
            //EliminateDueSymmetries удаляет части выражения, которые равны нулю из-за симметрии.
            symmetrized <<= Collect['N_ab'.t, 'H^ij'.t] & EliminateDueSymmetries
            //Статический метод info из класса TensorUtils с параметром symmetrized возвращает информацию
            // об объекте symmetrized.
            println(TensorUtils.info(symmetrized))
            //Произведение матрицы W и поля H: W^{a}_{p,q,c}*H^{c}_{a}
            println symmetrized[0].toString(LaTeX)
            // Что он выводит тут ?
            // symmetrized имеет тип Tensor. В Redberry все мат. выражения имеют тип Tensor. 
            // symmetrized[0] -- первый элемент мат. выражения (для суммы первое слагаемое, для произведения первый множитель)
            // symmetrized[1] -- второй элемент мат. выражения (для суммы второе слагаемое, для произведения второй множитель)
            // symmetrized[2] -- третий и тп
            
            println symmetrized[1].toString(LaTeX)
        }
    }

    @Test
    //симметризация по паре индексов оператора с 4-мя индексами (1-е слагаемое оп. D1)
    void test2() {
        use(Redberry) {
            CC.resetTensorNames(1)

            addSymmetries 'R_abcd', -[[0, 1]].p, [[0, 2], [1, 3]].p
            setSymmetric 'R_ab'
            setSymmetric 'H_ab'

            'd_abcd := (1/2)*(d_ab*d_cd + d_ac*d_bd)'.t
            //Здесь записна часть матрицы К, соответствующая 1-му слагаемому оп. D1 (вместе c J с 4-мя индексами)
            def k = 'K_cm^en_{ab}^{gd} = (1/2)*(d_{ab}*d^{gd}+d_{a}^{g}*d_{b}^{d}-d_{a}^{d}*d_{b}^{g})*d_{cm}*d^{en}'.t

            //симм-ция проводится по индексам ab
            def symmetrized = symmetrizePair('K^abcd_{ijpq}'.t, 'N_abcd'.t, 'H^ij'.t, 0)
            symmetrized <<= k
            symmetrized <<= ExpandAndEliminate
            symmetrized <<= 'R^a_bac = R_bc'.t
            symmetrized <<= 'R^a_a = R'.t
            symmetrized <<= Collect['N_abcd'.t, 'H^ij'.t] & EliminateDueSymmetries

            println(TensorUtils.info(symmetrized))
            println(symmetrized)
            for(def term: symmetrized)
                println(term)
            //println symmetrized[0].toString(LaTeX)
            //println symmetrized[1].toString(LaTeX)
            //println symmetrized[2].toString(LaTeX)
            //println symmetrized[3].toString(LaTeX)

        }
    }

    //проверка цикла, кот. будет брать рез-т симм-ции по паре инд. и снова симм-ть, смещаясь на 1 инд. влево или вправо
    @Test
    void test3() {
        use(Redberry) {
            CC.resetTensorNames(1)

            addSymmetries 'R_abcd', -[[0, 1]].p, [[0, 2], [1, 3]].p
            setSymmetric 'R_ab'
            setSymmetric 'H_ab'

            'd_abcd := (1/2)*(d_ab*d_cd + d_ac*d_bd)'.t
            //Здесь записна часть матрицы К, соответствующая 1-му слагаемому оп. D1
            def k = 'K_cm^en_{ab}^{gd} = (1/2)*(d_{ab}*d^{gd}+d_{a}^{g}*d_{b}^{d}-d_{a}^{d}*d_{b}^{g})*d_{cm}*d^{en}'.t

            def symm = symmetrizeAll('K^abcd_{ijpq}'.t, '^abcd'.si)
            symm <<= k
            symm <<= ExpandAndEliminate
            symm <<= 'R^a_bac = R_bc'.t
            symm <<= 'R^a_a = R'.t
            symm <<= Collect['N_bc'.t, 'H^ij'.t] & EliminateDueSymmetries

            println(TensorUtils.info(symm))
            println(symm)
            for(def term: symm)
                    println(term)
        }
    }
}
