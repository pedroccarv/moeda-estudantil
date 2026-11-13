import { useEffect, useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { api } from '../api'
import { Faixa } from '../componentes/Ui'
import { useSessao } from '../sessao'

export default function PainelProfessor() {
  const { setSaldo } = useSessao()
  const location = useLocation()
  const [professor, setProfessor] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => { document.title = 'Meu balcão' }, [])

  useEffect(() => {
    let ativo = true
    api('/api/professor')
      .then((dados) => {
        if (!ativo) return
        setProfessor(dados)
        setSaldo(dados.saldoMoedas)
      })
      .catch((e) => { if (ativo) setErro(e.message) })
    return () => { ativo = false }
  }, [setSaldo])

  if (!professor && !erro) return <p className="carregando">Carregando.</p>

  const semestre = professor?.ultimoPeriodoCreditado || 'ainda não creditado'

  return (
    <>
      <Faixa ok={location.state?.mensagem} aviso={professor?.aviso} erro={erro} />
      {professor ? (
        <>
          <h1>{professor.nome}</h1>
          <p>{professor.departamento}</p>
          <p>{professor.instituicaoNome}</p>
          <div className="resumo">
            <p className="saldo"><span>{professor.saldoMoedas}</span><small>moedas</small></p>
            <div className="acoes">
              <Link className="botao" to="/professor/enviar">Enviar moedas</Link>
              <Link className="botao secundario" to="/professor/extrato">Ver extrato</Link>
            </div>
          </div>
          <p className="prosa">A cada semestre o sistema soma 1.000 moedas ao saldo.</p>
          <p>Semestre reconhecido: {semestre}</p>
        </>
      ) : null}
    </>
  )
}
