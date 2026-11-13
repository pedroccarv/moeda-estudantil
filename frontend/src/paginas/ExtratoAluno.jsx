import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { api } from '../api'
import { Emails, Tabela } from '../componentes/Listas'
import { Faixa } from '../componentes/Ui'
import { useSessao } from '../sessao'
import { quando } from '../util'

export default function ExtratoAluno() {
  const { setSaldo } = useSessao()
  const location = useLocation()
  const [extrato, setExtrato] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => { document.title = 'Extrato' }, [])

  useEffect(() => {
    let ativo = true
    api('/api/aluno/extrato')
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
      <Faixa ok={location.state?.mensagem} erro={erro} />
      <h1>Extrato</h1>
      <p className="saldo"><span>{extrato.saldoMoedas}</span><small>moedas</small></p>
      <h2>Moedas recebidas</h2>
      <Tabela
        colunas={['Data', 'Valor', 'Professor', 'Motivo']}
        vazio="Nenhuma moeda recebida."
        linhas={extrato.recebimentos.map((item) => (
          <tr key={`${item.dataHora}-${item.motivo}`}>
            <td>{quando(item.dataHora)}</td>
            <td>{item.valor}</td>
            <td>{item.professorNome}</td>
            <td>{item.motivo}</td>
          </tr>
        ))}
      />
      <h2>Resgates</h2>
      <Tabela
        colunas={['Data', 'Valor', 'Vantagem', 'Empresa', 'Cupom']}
        vazio="Nenhum resgate."
        linhas={extrato.resgates.map((item) => (
          <tr key={item.codigoCupom}>
            <td>{quando(item.dataHora)}</td>
            <td>{item.valor}</td>
            <td>{item.vantagemDescricao}</td>
            <td>{item.empresaNome}</td>
            <td className="cupom">{item.codigoCupom}</td>
          </tr>
        ))}
      />
      <Emails lista={extrato.emails} />
    </>
  )
}
