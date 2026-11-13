import { useEffect, useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { Campo, Faixa } from '../componentes/Ui'
import { useSessao } from '../sessao'
import { destino } from '../util'

export default function Entrar() {
  const { sessao, setSessao } = useSessao()
  const navigate = useNavigate()
  const location = useLocation()
  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)

  useEffect(() => { document.title = 'Entrar' }, [])
  if (sessao) return <Navigate to={destino(sessao.papel)} replace />

  async function enviar(event) {
    event.preventDefault()
    setEnviando(true)
    setErro('')
    try {
      const nova = await api('/api/sessao', { method: 'POST', json: { email, senha } })
      setSessao(nova)
      navigate(destino(nova.papel), { replace: true })
    } catch (e) {
      setErro(e.message)
      setEnviando(false)
    }
  }

  return (
    <div className="pagina-form">
      <Faixa ok={location.state?.mensagem} erro={erro} />
      <h1>Entrar</h1>
      <form onSubmit={enviar}>
        <Campo id="email" rotulo="E-mail" tipo="email" valor={email} aoMudar={setEmail} obrigatorio autoComplete="username" />
        <Campo id="senha" rotulo="Senha" tipo="password" valor={senha} aoMudar={setSenha} obrigatorio autoComplete="current-password" />
        <div className="acoes">
          <button className="botao" type="submit" disabled={enviando}>Entrar</button>
        </div>
      </form>
      <p>Ainda não tem conta?</p>
      <div className="acoes">
        <Link className="botao secundario" to="/cadastro/aluno">Cadastrar aluno</Link>
        <Link className="botao secundario" to="/cadastro/empresa">Cadastrar empresa</Link>
      </div>
      <p className="prosa">Professores de demonstração, senha prof123: ana.souza@pucminas.br e carlos.lima@ufmg.br.</p>
    </div>
  )
}
