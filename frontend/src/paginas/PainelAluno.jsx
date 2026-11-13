import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { Faixa } from '../componentes/Ui'
import { useSessao } from '../sessao'

export default function PainelAluno() {
  const { setSaldo, setSessao } = useSessao()
  const location = useLocation()
  const navigate = useNavigate()
  const [aluno, setAluno] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => { document.title = 'Meu balcão' }, [])

  useEffect(() => {
    let ativo = true
    api('/api/aluno')
      .then((dados) => {
        if (!ativo) return
        setAluno(dados)
        setSaldo(dados.saldoMoedas)
      })
      .catch((e) => { if (ativo) setErro(e.message) })
    return () => { ativo = false }
  }, [setSaldo])

  async function excluir() {
    if (!window.confirm('Excluir seu cadastro?')) return
    try {
      const resposta = await api('/api/aluno', { method: 'DELETE' })
      setSessao(null)
      setSaldo(null)
      navigate('/entrar', { state: { mensagem: resposta.mensagem } })
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!aluno && !erro) return <p className="carregando">Carregando.</p>

  return (
    <>
      <Faixa ok={location.state?.mensagem} erro={erro} />
      {aluno ? (
        <>
          <h1>{aluno.nome}</h1>
          <p>{aluno.curso}</p>
          <p>{aluno.instituicaoNome}</p>
          <div className="resumo">
            <p className="saldo"><span>{aluno.saldoMoedas}</span><small>moedas</small></p>
            <div className="acoes">
              <Link className="botao" to="/aluno/vantagens">Resgatar vantagem</Link>
              <Link className="botao secundario" to="/aluno/extrato">Ver extrato</Link>
              <Link className="botao secundario" to="/aluno/editar">Editar meus dados</Link>
            </div>
          </div>
          <button className="botao perigo" type="button" onClick={excluir}>Excluir meu cadastro</button>
        </>
      ) : null}
    </>
  )
}
