import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api'
import { Faixa, Foto } from '../componentes/Ui'
import { useSessao } from '../sessao'
import { fotoSegura } from '../util'

export default function Vantagens() {
  const { setSaldo } = useSessao()
  const navigate = useNavigate()
  const [dados, setDados] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => { document.title = 'Vantagens' }, [])

  useEffect(() => {
    let ativo = true
    api('/api/aluno/vantagens')
      .then((resposta) => {
        if (!ativo) return
        setDados(resposta)
        setSaldo(resposta.saldoMoedas)
      })
      .catch((e) => { if (ativo) setErro(e.message) })
    return () => { ativo = false }
  }, [setSaldo])

  async function resgatar(id) {
    if (!window.confirm('Resgatar esta vantagem?')) return
    try {
      const feito = await api(`/api/aluno/vantagens/${id}/resgatar`, { method: 'POST' })
      navigate('/aluno/extrato', { state: { mensagem: feito.mensagem } })
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!dados && !erro) return <p className="carregando">Carregando.</p>

  return (
    <>
      <Faixa erro={erro} />
      <h1>Vantagens</h1>
      {dados ? <p className="saldo"><span>{dados.saldoMoedas}</span><small>moedas</small></p> : null}
      {dados && dados.vantagens.length === 0 ? <p>Nenhuma vantagem cadastrada ainda.</p> : null}
      <div className="catalogo">
        {dados ? dados.vantagens.map((vantagem) => {
          const pode = dados.saldoMoedas >= vantagem.custoEmMoedas
          return (
            <article className="prato" key={vantagem.id}>
              <Foto url={fotoSegura(vantagem.fotoUrl)} alt="Foto da vantagem" />
              <div>
                <h2>{vantagem.descricao}</h2>
                <p>{vantagem.empresaNome}</p>
                {!pode ? <p>Saldo insuficiente para esta vantagem.</p> : null}
              </div>
              <div className="preco-acao">
                <p className="preco">{vantagem.custoEmMoedas} moedas</p>
                <button className="botao" type="button" disabled={!pode} onClick={() => resgatar(vantagem.id)}>Resgatar</button>
              </div>
            </article>
          )
        }) : null}
      </div>
    </>
  )
}
