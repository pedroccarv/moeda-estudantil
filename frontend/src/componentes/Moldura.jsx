import { NavLink, Outlet, useNavigate } from 'react-router-dom'
import { useSessao } from '../sessao'
import { destino, rotuloPapel } from '../util'

const LINKS = {
  ALUNO: [
    ['/aluno', 'Meu balcão', true],
    ['/aluno/extrato', 'Extrato', false],
    ['/aluno/vantagens', 'Vantagens', false],
  ],
  PROFESSOR: [
    ['/professor', 'Meu balcão', true],
    ['/professor/enviar', 'Enviar moedas', false],
    ['/professor/extrato', 'Extrato', false],
  ],
  EMPRESA: [
    ['/empresa', 'Meu balcão', true],
    ['/empresa/vantagens/nova', 'Nova vantagem', false],
    ['/empresa/editar', 'Meus dados', false],
  ],
}

export default function Moldura() {
  const { sessao, sair } = useSessao()
  const navigate = useNavigate()
  const home = sessao ? destino(sessao.papel) : '/'

  async function encerrar() {
    const mensagem = await sair()
    navigate('/entrar', { state: { mensagem } })
  }

  return (
    <>
      <a className="pular" href="#conteudo">Ir para o conteúdo</a>
      <header className="topo">
        <NavLink className="marca" to={home}>Moeda</NavLink>
        {sessao ? <p className="identidade">{sessao.nome}, {rotuloPapel(sessao.papel)}</p> : <span className="identidade" />}
        {!sessao && sessao !== undefined ? (
          <nav className="topo-links" aria-label="Acesso">
            <NavLink to="/entrar">Entrar</NavLink>
            <NavLink to="/cadastro/aluno">Cadastrar aluno</NavLink>
            <NavLink to="/cadastro/empresa">Cadastrar empresa</NavLink>
          </nav>
        ) : null}
      </header>
      <div className={sessao ? 'corpo com-menu' : 'corpo'}>
        {sessao ? (
          <nav className="menu" aria-label="Seções">
            {LINKS[sessao.papel].map(([to, rotulo, fim]) => (
              <NavLink key={to} to={to} end={fim} className={({ isActive }) => (isActive ? 'ativo' : undefined)}>
                {rotulo}
              </NavLink>
            ))}
            <button className="sair" type="button" onClick={encerrar}>Sair</button>
          </nav>
        ) : null}
        <main className="miolo" id="conteudo">
          {sessao === undefined ? <p className="carregando">Carregando.</p> : <Outlet />}
        </main>
      </div>
    </>
  )
}
