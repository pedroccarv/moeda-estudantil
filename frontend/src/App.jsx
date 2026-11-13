import { Route, Routes } from 'react-router-dom'
import Moldura from './componentes/Moldura'
import { Exige } from './componentes/Ui'
import Entrar from './paginas/Entrar'
import Enviar from './paginas/Enviar'
import ExtratoAluno from './paginas/ExtratoAluno'
import ExtratoProfessor from './paginas/ExtratoProfessor'
import FormAluno from './paginas/FormAluno'
import FormEmpresa from './paginas/FormEmpresa'
import FormVantagem from './paginas/FormVantagem'
import Inicio from './paginas/Inicio'
import PainelAluno from './paginas/PainelAluno'
import PainelEmpresa from './paginas/PainelEmpresa'
import PainelProfessor from './paginas/PainelProfessor'
import Vantagens from './paginas/Vantagens'

export default function App() {
  return (
    <Routes>
      <Route element={<Moldura />}>
        <Route index element={<Inicio />} />
        <Route path="entrar" element={<Entrar />} />
        <Route path="cadastro/aluno" element={<FormAluno />} />
        <Route path="cadastro/empresa" element={<FormEmpresa />} />
        <Route path="aluno" element={<Exige papel="ALUNO"><PainelAluno /></Exige>} />
        <Route path="aluno/extrato" element={<Exige papel="ALUNO"><ExtratoAluno /></Exige>} />
        <Route path="aluno/vantagens" element={<Exige papel="ALUNO"><Vantagens /></Exige>} />
        <Route path="aluno/editar" element={<Exige papel="ALUNO"><FormAluno editando /></Exige>} />
        <Route path="professor" element={<Exige papel="PROFESSOR"><PainelProfessor /></Exige>} />
        <Route path="professor/enviar" element={<Exige papel="PROFESSOR"><Enviar /></Exige>} />
        <Route path="professor/extrato" element={<Exige papel="PROFESSOR"><ExtratoProfessor /></Exige>} />
        <Route path="empresa" element={<Exige papel="EMPRESA"><PainelEmpresa /></Exige>} />
        <Route path="empresa/editar" element={<Exige papel="EMPRESA"><FormEmpresa editando /></Exige>} />
        <Route path="empresa/vantagens/nova" element={<Exige papel="EMPRESA"><FormVantagem /></Exige>} />
        <Route path="empresa/vantagens/:id" element={<Exige papel="EMPRESA"><FormVantagem /></Exige>} />
        <Route path="*" element={<p>Essa página não existe.</p>} />
      </Route>
    </Routes>
  )
}
