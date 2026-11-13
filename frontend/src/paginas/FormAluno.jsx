import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { api } from '../api'
import { Campo, Faixa } from '../componentes/Ui'
import { useSessao } from '../sessao'

const VAZIO = { nome: '', email: '', senha: '', cpf: '', rg: '', endereco: '', curso: '', instituicaoId: '' }

export default function FormAluno({ editando = false }) {
  const { setSessao } = useSessao()
  const navigate = useNavigate()
  const [valores, setValores] = useState(VAZIO)
  const [instituicoes, setInstituicoes] = useState([])
  const [erro, setErro] = useState('')
  const [enviando, setEnviando] = useState(false)
  const [pronto, setPronto] = useState(!editando)

  useEffect(() => {
    document.title = editando ? 'Editar meus dados' : 'Cadastrar aluno'
  }, [editando])

  useEffect(() => {
    let ativo = true
    async function carregar() {
      try {
        const lista = await api('/api/instituicoes')
        if (!ativo) return
        setInstituicoes(lista)
        if (editando) {
          const aluno = await api('/api/aluno')
          if (!ativo) return
          setValores({ ...aluno, instituicaoId: String(aluno.instituicaoId), senha: '' })
        }
        setPronto(true)
      } catch (e) {
        if (ativo) setErro(e.message)
      }
    }
    carregar()
    return () => { ativo = false }
  }, [editando])

  function mudar(campo) {
    return (valor) => setValores((atual) => ({ ...atual, [campo]: valor }))
  }

  async function enviar(event) {
    event.preventDefault()
    setEnviando(true)
    setErro('')
    const pedido = { ...valores, instituicaoId: valores.instituicaoId ? Number(valores.instituicaoId) : null }
    try {
      const resposta = await api(editando ? '/api/aluno' : '/api/alunos', {
        method: editando ? 'PUT' : 'POST',
        json: pedido,
      })
      if (editando) {
        const atual = await api('/api/sessao')
        setSessao(atual)
        navigate('/aluno', { state: { mensagem: resposta.mensagem } })
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
      <h1>{editando ? 'Editar meus dados' : 'Cadastrar aluno'}</h1>
      <form onSubmit={enviar}>
        <Campo id="nome" rotulo="Nome" valor={valores.nome} aoMudar={mudar('nome')} obrigatorio autoComplete="name" />
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
        <Campo id="cpf" rotulo="CPF" valor={valores.cpf} aoMudar={mudar('cpf')} obrigatorio />
        <Campo id="rg" rotulo="RG" valor={valores.rg} aoMudar={mudar('rg')} obrigatorio />
        <Campo id="endereco" rotulo="Endereço" valor={valores.endereco} aoMudar={mudar('endereco')} obrigatorio autoComplete="street-address" />
        <Campo id="curso" rotulo="Curso" valor={valores.curso} aoMudar={mudar('curso')} obrigatorio />
        <div className="campo">
          <label htmlFor="instituicaoId">Instituição de ensino</label>
          <select id="instituicaoId" value={valores.instituicaoId} required onChange={(event) => mudar('instituicaoId')(event.target.value)}>
            <option value="">Selecione</option>
            {instituicoes.map((item) => <option key={item.id} value={item.id}>{item.nome}</option>)}
          </select>
        </div>
        <div className="acoes">
          <button className="botao" type="submit" disabled={enviando}>Salvar</button>
          <Link className="botao secundario" to={editando ? '/aluno' : '/'}>Cancelar</Link>
        </div>
      </form>
    </div>
  )
}
