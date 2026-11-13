import { useEffect } from 'react'
import { Link, Navigate } from 'react-router-dom'
import { useSessao } from '../sessao'
import { destino } from '../util'

export default function Inicio() {
  const { sessao } = useSessao()
  useEffect(() => { document.title = 'Moeda estudantil' }, [])
  if (sessao) return <Navigate to={destino(sessao.papel)} replace />

  return (
    <div className="abertura">
      <div>
        <h1>Moeda estudantil</h1>
        <p className="prosa">Professores distribuem moedas aos alunos como reconhecimento. O aluno troca essas moedas por vantagens de empresas parceiras.</p>
        <div className="acoes">
          <Link className="botao" to="/entrar">Entrar</Link>
          <Link className="botao secundario" to="/cadastro/aluno">Cadastrar aluno</Link>
          <Link className="botao secundario" to="/cadastro/empresa">Cadastrar empresa</Link>
        </div>
        <p className="prosa">Professores já entram com a conta enviada pela instituição. Alunos e empresas se cadastram aqui.</p>
      </div>
      <div className="passos">
        <p>O professor envia moedas e registra o motivo.</p>
        <p>O aluno troca o saldo por uma vantagem.</p>
        <p>A empresa confere o cupom na hora da troca.</p>
      </div>
    </div>
  )
}
