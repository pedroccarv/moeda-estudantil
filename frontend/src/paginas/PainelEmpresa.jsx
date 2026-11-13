import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { Emails, Tabela } from '../componentes/Listas'
import { Faixa, Foto } from '../componentes/Ui'
import { useSessao } from '../sessao'
import { fotoSegura, quando } from '../util'

export default function PainelEmpresa() {
  const { setSessao } = useSessao()
  const location = useLocation()
  const navigate = useNavigate()
  const [painel, setPainel] = useState(null)
  const [erro, setErro] = useState('')

  useEffect(() => { document.title = 'Meu balcão' }, [])

  function carregar() {
    return api('/api/empresa').then(setPainel).catch((e) => setErro(e.message))
  }

  useEffect(() => {
    let ativo = true
    api('/api/empresa')
      .then((dados) => { if (ativo) setPainel(dados) })
      .catch((e) => { if (ativo) setErro(e.message) })
    return () => { ativo = false }
  }, [])

  async function excluirVantagem(id) {
    if (!window.confirm('Excluir esta vantagem?')) return
    try {
      const resposta = await api(`/api/empresa/vantagens/${id}`, { method: 'DELETE' })
      setErro('')
      await carregar()
      navigate('/empresa', { replace: true, state: { mensagem: resposta.mensagem } })
    } catch (e) {
      setErro(e.message)
    }
  }

  async function excluirCadastro() {
    if (!window.confirm('Excluir o cadastro da empresa?')) return
    try {
      const resposta = await api('/api/empresa', { method: 'DELETE' })
      setSessao(null)
      navigate('/entrar', { state: { mensagem: resposta.mensagem } })
    } catch (e) {
      setErro(e.message)
    }
  }

  if (!painel && !erro) return <p className="carregando">Carregando.</p>

  return (
    <>
      <Faixa ok={location.state?.mensagem} erro={erro} />
      {painel ? (
        <>
          <h1>{painel.empresa.nome}</h1>
          <p>{painel.empresa.email}</p>
          <div className="acoes">
            <Link className="botao" to="/empresa/vantagens/nova">Nova vantagem</Link>
            <Link className="botao secundario" to="/empresa/editar">Editar cadastro</Link>
          </div>
          <h2>Vantagens</h2>
          {painel.vantagens.length === 0 ? <p>Nenhuma vantagem cadastrada. Inclua descrição, foto e custo em moedas.</p> : null}
          <div className="catalogo">
            {painel.vantagens.map((vantagem) => (
              <article className="prato" key={vantagem.id}>
                <Foto url={fotoSegura(vantagem.fotoUrl)} alt="Foto da vantagem" />
                <div>
                  <h2>{vantagem.descricao}</h2>
                  <div className="acoes">
                    <Link className="botao secundario" to={`/empresa/vantagens/${vantagem.id}`}>Editar</Link>
                    <button className="botao perigo" type="button" onClick={() => excluirVantagem(vantagem.id)}>Excluir</button>
                  </div>
                </div>
                <p className="preco">{vantagem.custoEmMoedas} moedas</p>
              </article>
            ))}
          </div>
          <h2>Resgates para conferência</h2>
          <Tabela
            colunas={['Data', 'Aluno', 'Vantagem', 'Cupom']}
            vazio="Nenhum resgate ainda."
            linhas={painel.resgates.map((item) => (
              <tr key={item.codigoCupom}>
                <td>{quando(item.dataHora)}</td>
                <td>{item.alunoNome}</td>
                <td>{item.vantagemDescricao}</td>
                <td className="cupom">{item.codigoCupom}</td>
              </tr>
            ))}
          />
          <Emails lista={painel.emails} />
          <button className="botao perigo solto" type="button" onClick={excluirCadastro}>Excluir meu cadastro</button>
        </>
      ) : null}
    </>
  )
}
