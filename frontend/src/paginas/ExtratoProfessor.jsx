import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { api } from '../api'
import { Emails, Tabela } from '../componentes/Listas'
import { Faixa } from '../componentes/Ui'
import { useSessao } from '../sessao'
import { quando } from '../util'

export default function ExtratoProfessor() {
  const { setSaldo } = useSessao()
  const location = useLocation()
  const [extrato, setExtrato] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => { document.title = 'Extrato' }, [])

  useEffect(() => {
    let ativo = true
    api('/api/professor/extrato')
      .then((dados) => {
        if (!ativo) return
        setExtrato(dados)
        setSaldo(dados.saldoMoedas)
      })
      .catch((e) => { if (ativo) setErro(e.message) })
    return () => { ativo = false }
  }, [setSaldo])

  if (!extrato && !erro) return <p className="carregando">Carregando.</p>
  if (!extrato) return <Faixa erro={erro} />

  return (
    <>
      <Faixa ok={location.state?.mensagem} aviso={extrato.aviso} erro={erro} />
      <h1>Extrato</h1>
      <p className="saldo"><span>{extrato.saldoMoedas}</span><small>moedas</small></p>
      <Tabela
        colunas={['Data', 'Valor', 'Aluno', 'Motivo']}
        vazio="Nenhum envio."
        linhas={extrato.envios.map((item) => (
          <tr key={`${item.dataHora}-${item.alunoNome}`}>
            <td>{quando(item.dataHora)}</td>
            <td>{item.valor}</td>
            <td>{item.alunoNome}</td>
            <td>{item.motivo}</td>
          </tr>
        ))}
      />
      <Emails lista={extrato.emails} />
    </>
  )
}
