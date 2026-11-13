import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { Campo, Faixa } from '../componentes/Ui'
import { useSessao } from '../sessao'

const VAZIO = { nome: '', email: '', senha: '', cnpj: '' }

export default function FormEmpresa({ editando = false }) {
  const { setSessao } = useSessao()
  const navigate = useNavigate()
  const [valores, setValores] = useState(VAZIO)
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [pronto, setPronto] = useState(!editando)

  useEffect(() => {
    document.title = editando ? 'Meus dados' : 'Cadastrar empresa'
  }, [editando])

  useEffect(() => {
    if (!editando) return undefined
    let ativo = true
    api('/api/empresa')
      .then((painel) => {
        if (!ativo) return
        setValores({ ...painel.empresa, cnpj: painel.empresa.cnpj || '', senha: '' })
        setPronto(true)
      })
      .catch((e) => { if (ativo) setErro(e.message) })
    return () => { ativo = false }
  }, [editando])

  function mudar(campo) {
    return (valor) => setValores((atual) => ({ ...atual, [campo]: valor }))
  }

  async function enviar(event) {
    event.preventDefault()
    setEnviando(true)
    setErro('')
    try {
      const resposta = await api(editando ? '/api/empresa' : '/api/empresas', {
        method: editando ? 'PUT' : 'POST',
        json: valores,
      })
      if (editando) {
        setSessao(await api('/api/sessao'))
        navigate('/empresa', { state: { mensagem: resposta.mensagem } })
      } else {
        navigate('/entrar', { state: { mensagem: resposta.mensagem } })
      }
    } catch (e) {
      setErro(e.message)
      setEnviando(false)
    }
  }

  if (!pronto && !erro) return <p className="carregando">Carregando.</p>

  return (
    <div className="pagina-form">
      <Faixa erro={erro} />
      <h1>{editando ? 'Meus dados' : 'Cadastrar empresa'}</h1>
      <form onSubmit={enviar}>
        <Campo id="nome" rotulo="Nome" valor={valores.nome} aoMudar={mudar('nome')} obrigatorio autoComplete="organization" />
        <Campo id="email" rotulo="E-mail" tipo="email" valor={valores.email} aoMudar={mudar('email')} obrigatorio autoComplete="email" />
        <Campo
          id="senha"
          rotulo="Senha"
          tipo="password"
          valor={valores.senha}
          aoMudar={mudar('senha')}
          obrigatorio={!editando}
          autoComplete="new-password"
          placeholder={editando ? 'Deixe em branco para manter' : undefined}
        />
        <Campo id="cnpj" rotulo="CNPJ" valor={valores.cnpj} aoMudar={mudar('cnpj')} />
        {editando ? null : <p className="prosa">Depois de entrar, cadastre as vantagens com descrição, foto e custo em moedas.</p>}
        <div className="acoes">
          <button className="botao" type="submit" disabled={enviando}>Salvar</button>
          <Link className="botao secundario" to={editando ? '/empresa' : '/'}>Cancelar</Link>
        </div>
      </form>
    </div>
  )
}
