//Чтобы указать, что класс принадлежит определенному пакету, надо использовать директиву package,
// после которой указывается имя пакета. У нас пакет - cc.redberry.onloopsym. Мы можем импортировать пакеты и классы
// в проект с помощью директивы import, которая указывается после директивы package.
package cc.redberry.onloopsym

import cc.redberry.core.indexgenerator.IndexGeneratorImpl
import cc.redberry.core.indices.IndicesFactory
import cc.redberry.core.indices.SimpleIndices
import cc.redberry.core.tensor.SimpleTensor
import cc.redberry.core.tensor.Tensor
import cc.redberry.core.groups.permutations.PermutationGroup
import cc.redberry.core.utils.TensorUtils
import cc.redberry.groovy.Redberry

//import static cc.redberry.core.context.OutputFormat.*
import static cc.redberry.core.tensor.Tensors.simpleTensor
import static cc.redberry.groovy.RedberryStatic.ExpandAndEliminate
import static cc.redberry.groovy.RedberryStatic.Symmetrize

//Особая форма импорта - статический импорт. Для этого вместе с директивой import используется модификатор static.
// Если классы имеют статические методы, то благодаря операции статического импорта мы можем использовать
// эти методы без названия класса. Например, писать не Math.sqrt(20), а sqrt(20), так как функция sqrt(),
// которая возвращает квадратный корень числа, является статической.
//В объектно-ориентированной программе с применением классов каждый объект является «экземпляром»
// некоторого конкретного класса. Практически класс может пониматься как некий шаблон,
// по которому создаются объекты — экземпляры данного класса.
//У нас class SymmetrizeOp ("Симметризовать оператор", имя класса всегда с большой буквы, если имя составное,
// то кажд. первая буква - большая; груви чувствителен к регистру)
class SymmetrizeOp {
//Статические члены класса могут использоваться без создания объектов класса. Статические методы
// static def covariantCommutator() и static def symmetrizePair() (первая буква названия метода
// всегда маленькая, если имя составное, остальные буквы большие). static - модификатор,
// def - тип возвращаемого значения. Если переменные и константы хранят некоторые
// значения, то методы содержат собой набор операторов, которые выполняют
// определенные действия, а в фигурные скобки заключено тело метода - все действия, которые он выполняет.
// Параметры метода представляют собой переменные, которые определяются в сигнатуре метода и создаются при его вызове.
    
// поправка: def - это _вместо_ типа возвращаемого значения, если мы пишем "def", компилятор сам определит какой 
// тип возвращается, когда будет вызван метод. Вместо "def" мы могли бы написать реальный тип который мы вернем, 
// в данном случае это тип Tensor. См. "статическая типизация" и "динамическая типизация" на википедии
    static def covariantCommutator(SimpleIndices dIndices, Tensor expression) {
        // Assert — это специальная конструкция, позволяющая проверять предположения о значениях произвольных данных в
        // произвольном месте программы. Эта конструкция может автоматически сигнализировать при обнаружении некорректных
        // данных, что обычно приводит к аварийному завершению программы с указанием места обнаружения некорректных данных.
        // Assert'ы визуально выделяются из общего кода и несут важную информацию о предположениях, на основе которых
        // работает данный код. Правильно расставленные assert'ы способны заменить большинство комментариев в коде.
        // Т.к. assert'ы могут быть удалены на этапе компиляции либо во время исполнения программы, они не должны менять
        // поведение программы. Если в результате удаления assert'а поведение программы может измениться, то это явный
        // признак неправильного использования assert'а. Таким образом, внутри assert'а нельзя вызывать функции,
        // изменяющие состояние программы либо внешнего окружения программы.
        assert dIndices.size() == 2

        //covariantCommutator - Идентификатор-метода (определяет имя метода)
        //Этот метод создаёт коммутатор ковариантных производных.
        //SimpleIndices dIndices, Tensor expression - входные параметры метода covariantCommutator
        //Входные параметры — это какие-либо данные, которые передаются из других классов (в данном случае
        // import cc.redberry.core.indices.SimpleIndices и import cc.redberry.core.tensor.Tensor)
        // и которые  метод должен обработать.
        //SimpleIndices и Tensor - типы переменных (какого они класса),
        //dIndices и expression - названия (идентификаторы) переменных
        use(Redberry) { //указываем, что будем использовать синтаксис определенный в классе Redberry (import cc.redberry.groovy.Redberry)
            //Создаём переменные upper и lower, присваиваем им переменную expression (из класса Tensor),
            // которая получает индексы (.indices) верхние или нижние (.upper или .lower) и свободные (.free)
            //def (ключевое слово) - объект (вводит переменную или метод)
            
            // поправка: def - это _вместо_ объявления типа локальной переменной
            // def i = 2 // тип i автоматически определится как Integer когда кто-то будет использолвана переменная i
            // def s = "abc" // тип i автоматически определится как String когда кто-то будет использолвана переменная s и тп
            // вместо def можно явно писать тип напр Integer или String или Tensor  и тп

            
            //Для того, чтобы обратиться к члену класса, необходимо указать его имя после имени объекта через точку.
            //indices, upper, lower, free - члены класса Redberry
            def upper = expression.indices.free.upper.si
            def lower = expression.indices.free.lower.si
            //Есть класс SymmetrizeOp. Есть public final класс TensorUtils (который выше был импортирован в проект).
            //В классе SymmetrizeOp вызываем метод, который описан в TensorUtils -
            // статический метод getAllIndicesNamesT с параметром expression.
            //Создаём переменную forbiddenIndices (запрещённые индексы), присваиваем ей этот метод.
            
            // попрвака: присваеваем не метод, а результат вызова метода; мы  _вызывыаем_ метод getAllIndicesNamesT 
            // с аргументом expression и результат выполнения кладем в переменную forbiddenIndices

            // ЧТО ДЕЛАЕТ МЕТОД getAllIndicesNamesT ?
            // метод getAllIndicesNamesT возвращает множество вообще всех индексов которые где-либо встречаются в выражении

            def forbiddenIndices = TensorUtils.getAllIndicesNamesT(expression)

            // ЧТО ДЕЛАЮТ МЕТОДЫ addAll И toArray ?
            // addAll добавляет все элменты массива в множество
            // toArray превращает объект Indices просто в массив одиночных индексов

            //Переменная forbiddenIndices обращается к методу addAll, в параметрах которой
            // переменная dIndices обращается к методу toArray
            // здесь все индексы dIndices мы тоже добавляем в множество запрещенных индексов хранящееся в перпеменной forbiddenIndices
            forbiddenIndices.addAll(dIndices.toArray())
            //Оператор new создает экземпляр (переменную ig) указанного класса (в данном случае
            // класса IndexGeneratorImpl) и возвращает ссылку на вновь созданный объект.
            def ig = new IndexGeneratorImpl(forbiddenIndices.toArray())

            //Создаём переменную dummyIndex (немой индекс) и присваиваем ей статический метод create
            // из класса IndicesFactory. В параметрах метода create переменная ig обращается к методу
            // generate, в параметрах которого переменная dIndices обращается к ... ???

            // ЧТО ДЕЛАЮТ МЕТОДЫ create И generate ? что есть get(0).type ?
            // dIndices.get(0) -- первый индекс 
            // dIndices.get(0).type -- тип первого индекса (греческий, латинский тп, закодировано в одном байте)

            def iType = dIndices.get(0).type

            //.si создаёт SimpleIndices объект — упорядоченные индексы (каждый индекс имеет чёткую позицию)
            def dummyIndex = IndicesFactory.create(ig.generate(iType)).si
            //Создаём переменную result, которой присваиваем значение 0.
            //.t превращает обычные символы в компьютерный объект
            def result = 0.t
            //ПЕРВЫЙ ЦИКЛ:
            //int k = 0 — инициализация счетчика, k < upper.size() — условие, при котором будет выполняться цикл,
            // ++k — изменение счетчика, далее в фигурных скобках все действия, которые происходят в цикле.
            //Некий_тензор.size() - возвращает размер тензора, т. е. количество слагаемых в случае суммы,
            // количество аргументов в случае функции и т. д.
            //++k — префиксный инкремент — увеличивает значение операнда на 1, и возвращает новое значение
            // в отл. от k++, который увеличивает, но возвращает старое значение.
            for (int k = 0; k < upper.size(); ++k) {
                //Создаём переменную currentIndex (текущий индекс), присваиваем ей значение объекта upper.
                //Некий_тензор[i..j] - возвращает список элементов от i-го (включительно) по j-й (не включительно)??
                //.si создаёт SimpleIndices объект — упорядоченные индексы
                def currentIndex = upper[k..k]
                //Создаём переменную riemannIndices, которой присваиваем значение суммы текущих, немых и dIndices
                //Как сумма SimpleIndices римановы индексы также будут SimpleIndices.
                def riemannIndices = currentIndex + dummyIndex + dIndices
                //+= (Оператор присваивания «Добавления», он присваивает левому операнду значения правого,
                // C += A, эквивалентно C = C + A)
                //% (Делит левый операнд на правый операнд и возвращает остаток)
                //>> (Бинарный оператор сдвига вправо. Значение правых операндов перемещается вправо
                // на количество бит, заданных левых операндом)
                //.inverted - получать инвертированные индексы

                // currentIndex  = _i
                // dummyIndex = _k
                // dummyIndex.inverted = ^k
                // (currentIndex % dummyIndex.inverted)  =  (_i => ^k)
                // expression = F_ijk
                // (currentIndex % dummyIndex.inverted) >> expression  = (_i => ^k) >> F_ijk = F^k_jk

                result += simpleTensor("R", riemannIndices) * ((currentIndex % dummyIndex.inverted) >> expression)
            }

            for (int k = 0; k < lower.size(); ++k) {
                def currentIndex = lower[k..k]
                def riemannIndices = dummyIndex.inverted + currentIndex + dIndices
                //-= (Оператор присваивания «Вычитания», он вычитает из правого операнда левый операнд,
                // C -= A, эквивалентно C = C - A)
                result -= simpleTensor("R", riemannIndices) * ((currentIndex % dummyIndex) >> expression)
            }

            return result //Методы которые возвращают значения, также условно называют функциями.
            // Функции также отличаются тем, что мы обязательно должны использовать оператор return,
            // после которого ставится возвращаемое значение.
        }
    }

    //Этот метод симметризует выражение по паре индексов.
    //Tensor gTensor -- это любая из матриц K, W, M и т.д.
    //SimpleTensor nabla -- произведение ковариантных производных
    //Tensor hTensor -- поле h, на которое действует дифф. оператор
    static def symmetrizePair(Tensor gTensor, SimpleTensor nabla, Tensor hTensor, int iPosition) {
        //Tensor gTensor, SimpleTensor nabla, Tensor hTensor, int iPosition - параметры метода symmetrizePair.
        //SimpleTensor, Tensor и int - типы переменных, gTensor, nabla, hTensor и iPosition - названия переменных.
        use(Redberry) {
//        def product = gTensor * nabla * hTensor
//        if (product.indices.free.size() != 0)
//            throw new IllegalArgumentException("bad input: $gTensor * $nabla * $hTensor")

            //Симметризуем индексы в выражении по аналогии с - Симметризировать индексы a и b в выражении:
            //def indices = '_ab'.si
            //indices.symmetries.setSymmetric()
            //Создаём переменную toSymmetrize, которой присваиваем переменную nabla с инвертированными индексами.

            // ЧТО ТАКОЕ [iPosition, iPosition + 1] ?
            // nabla.indices[iPosition, iPosition + 1] --- возвращает индексы начиная с iPosition и до iPosition + 1 (включительно)

            def toSymmetrize = nabla.indices[iPosition, iPosition + 1].inverted
            println('toSymmetrize: ' + (toSymmetrize))
            //Свойство .symmetries позволяет определить перестановочные симметрии индексов. Оно
            // возвращает контейнер перестановок и соответствующую группу PermutationGroup.
            //Метод setSymmetric() устанавливает симметрии.
            // поправка: setSymmetric() делает индексы полностью симметричными
            toSymmetrize.symmetries.setSymmetric()
            //Создаём переменную higher, которой присваиваем произведение, в котором... ???

            // ЧТО ПРОИСХОДИТ С gTensor ?
            // (Symmetrize[toSymmetrize] >> gTensor) -- это применить симметризацию к тензору gTensor и вернуть резалт

            //Symmetrize[indices] — делает симметрии выражения одинаковыми с симметриями индексов,
            // делает выражение симметричным только по заданным индексам, также умножит результат
            // на симметрийный коэффициент.
            def higher = (Symmetrize[toSymmetrize] >> gTensor) * nabla * hTensor
            println('higher: ' + (higher << ExpandAndEliminate))
            //Создаём переменную lNabla, а дальше идёт условный (тернарный) оператор.
            //== (Проверяет, равны или нет значения двух операндов, если да, то условие становится истинным)
            //Тернарный оп-р (?:), переменная lNabla = (выражение) ? значение if true : значение if false
            // Тернарная операция имеет следующий синтаксис: [первый операнд - условие] ? [второй операнд]
            // : [третий операнд]. Таким образом, в этой операции участвуют сразу три операнда.
            // В зависимости от условия тернарная операция возвращает второй или третий операнд: если
            // условие равно true, то возвращается второй операнд; если условие равно false, то третий.
            // iPosition == 0 — первый операнд, 1.t — второй операнд
            // simpleTensor(nabla.stringName, nabla.indices[0..(iPosition - 1)]) — третий операнд
            // nabla.indices[0..(iPosition - 1)] — возвращает список элементов от 0-го по (iPosition - 1) ??
            def lNabla = iPosition == 0 ? 1.t : simpleTensor(nabla.stringName, nabla.indices[0..(iPosition - 1)])
            println('lNabla: ' + (lNabla))
            //Создаём переменную rNabla, дальше условный оператор.
            // iPosition == nabla.indices.size() - 2 — первый операнд, справа SimpleTensor nabla
            // получает индексы (.indices), .size() — возвращает размер тензора.
            // 1.t — второй операнд
            // simpleTensor(nabla.stringName, nabla.indices[(iPosition + 2)..(nabla.indices.size() - 1)]) — 3-тий оп.
            // simpleTensor — метод из класса Tensors, первый аргумент nabla.stringName,
            // второй агрумент — nabla.indices[(iPosition + 2)..(nabla.indices.size() - 1)],
            // возвращает список элементов от (iPosition + 2)-го по (nabla.indices.size() - 1).

            // ЧТО ТАКОЕ stringName ?

            def rNabla = iPosition == nabla.indices.size() - 2 ? 1.t : simpleTensor(nabla.stringName, nabla.indices[(iPosition + 2)..(nabla.indices.size() - 1)])
            println('rNabla: ' + (rNabla))
            //Создаём переменную nNabla, дальше условный оператор.
            // ((lNabla.indices + rNabla.indices).size() == 0) — первый операнд, 1.t — второй операнд.
            // simpleTensor(nabla.stringName, lNabla.indices + rNabla.indices) — 3-тий операнд.
            def nNabla = nabla.indices.size() == 2 ? 1.t : simpleTensor(nabla.stringName, lNabla.indices.si + rNabla.indices.si)
            println('nNabla: ' + (nNabla))
            //Создаём переменную subs, произведение правой и левой набл, которые потом сравниваются с nNabla

            // ЧТО ДЕЛАЕТ МЕТОД eq ?
            // a.eq(b) создает замену а -> b ; например 
            // def a = "x".t; def b = "y"; def subs = a.eq(b); все равно что написать сразу: def subs = "x = y".t;
            
            

            // lNabla * rNabla -> nNabla
            def subs = (lNabla * rNabla).eq(nNabla)

            def lower = gTensor * lNabla * covariantCommutator(nabla.indices[iPosition, iPosition + 1], rNabla * hTensor) / 2.t
            // <<= (Оператор присваивания «Сдвиг влево», C << = 2, это как C = C << 2).
            // & (Бинарный оператор AND копирует бит в результат, если он существует в обоих операндах).
            //ExpandAndEliminate разлагает произведение сумм и положительных целых степеней и,
            // по ходу дела, устраняет метрические тензоры и дельты Кронекера.
            //ExpandAndEliminate эквивалентен последовательному применению Expand & EliminateMetrics.
            //ExpandAndEliminate равен Expand [EliminateMetrics] & EliminateMetrics.
            //Когда нет метрических тензоров или дельт Кронекера, ExpandAndEliminate работает так же, как Expand.
            //ExpandAndEliminate [simplifications] или ExpandAndEliminate [[Simplifications: simplifications]]
            // будет применять дополнительные упрощения на каждом уровне процедуры разложения.

            //lower = lower << (ExpandAndEliminate & subs)
            //lower = (ExpandAndEliminate & subs) >> lower
            lower <<= ExpandAndEliminate & subs

            println('lower: ' + (lower))

            return higher + lower

        }
    }

    //метод с циклом для симметризации по всем индексам
    static def symmetrizeAll(Tensor expr, SimpleIndices indices) {

        use(Redberry) {

    def result = expr

    for (int i = 0; i < indices.size(); ++i) {

        result = symmetrizePair('K^abcd_{ijpq}'.t, 'N_abcd'.t, 'H^ij'.t, 0)
    }
    //symmetrizePair(Tensor, '^abcd'.si)
            println('result:' + result)

           return result

        }
    }

}
